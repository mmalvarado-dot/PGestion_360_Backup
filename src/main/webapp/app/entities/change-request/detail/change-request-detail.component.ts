import { Component, inject, input, effect } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import dayjs from 'dayjs';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { DataUtils } from 'app/core/util/data-util.service';
import { IChangeRequest } from '../change-request.model';

@Component({
  selector: 'jhi-change-request-detail',
  templateUrl: './change-request-detail.component.html',
  standalone: true,
  imports: [SharedModule, RouterModule, FormatMediumDatePipe, FormatMediumDatetimePipe, DatePipe],
})
export class ChangeRequestDetailComponent {
  changeRequest = input<IChangeRequest | null>(null);

  listaAvances: any[] = [];

  protected dataUtils = inject(DataUtils);
  private http = inject(HttpClient);

  constructor() {
    effect(() => {
      const req = this.changeRequest();
      if (req && req.id) {
        this.cargarAvances(req.id);
      }
    });
  }

  cargarAvances(requestId: number): void {
    this.http.get<any[]>(`/api/tracking-records/request/${requestId}`).subscribe({
      next: data => {
        this.listaAvances = data
          .map(avance => {
            if (avance.changeDate) {
              // JAQUE MATE: Le restamos obligatoriamente las 5 horas de diferencia (UTC)
              avance.changeDate = dayjs(avance.changeDate).subtract(5, 'hours');
            }

            // === HACK: EXTRACTOR DE NOMBRES DE ARCHIVOS ===
            let archivosExtraidos: any[] = [];
            let comentarioLimpio = avance.comments || '';

            if (comentarioLimpio.includes('Se adjuntó el archivo:')) {
              const lineas = comentarioLimpio.split('\n');
              const lineasRestantes: string[] = [];

              lineas.forEach((linea: string) => {
                if (linea.includes('Se adjuntó el archivo:')) {
                  const nombreArchivo = linea.replace('Se adjuntó el archivo:', '').trim();
                  if (nombreArchivo) {
                    // Mantenemos la estructura para que no se rompa el HTML
                    archivosExtraidos.push({ idAvance: avance.id, nombre: nombreArchivo });
                  }
                } else {
                  lineasRestantes.push(linea);
                }
              });
              comentarioLimpio = lineasRestantes.join('\n').trim();
            }

            avance.archivosVisuales = archivosExtraidos;
            avance.comentarioVisual = comentarioLimpio;

            return avance;
          })
          .filter(avance => avance.comentarioVisual && avance.comentarioVisual.includes('[Etapa:'))
          .sort((a, b) => (b.id ?? 0) - (a.id ?? 0));
      },
      error: error => {
        console.error('Error al cargar el historial de avances:', error);
      },
    });
  }

  // --- SOLUCIÓN DE DESCARGA DEFINITIVA ---
  downloadFile(idAvance: number, fileName: string): void {
    const reqId = this.changeRequest()?.id;
    if (!reqId) {
      alert('No se pudo identificar la solicitud actual.');
      return;
    }

    // PASO 1: Buscamos silenciosamente en tu "Registro de Archivos" los archivos de esta Solicitud
    this.http.get<any[]>(`/api/file-records?changeRequestId.equals=${reqId}&size=100`, { observe: 'response' }).subscribe({
      next: res => {
        const archivos = res.body || [];

        // Buscamos cuál de esos archivos tiene exactamente el mismo nombre que el de nuestro avance
        const archivoFisico = archivos.find((f: any) => f.fileName === fileName);

        if (archivoFisico && archivoFisico.id) {
          // PASO 2: ¡Bingo! Tenemos el ID real del archivo. Usamos tu ruta oficial para descargarlo.
          const urlOficial = `/api/change-requests/archivo/${archivoFisico.id}/descargar?descargar=true`;

          this.http.get(urlOficial, { responseType: 'blob', observe: 'response' }).subscribe({
            next: (response: HttpResponse<Blob>) => {
              if (!response.body) return;

              const fileUrl = window.URL.createObjectURL(response.body);
              const a = document.createElement('a');
              a.href = fileUrl;
              a.download = fileName;
              document.body.appendChild(a);
              a.click();
              document.body.removeChild(a);

              setTimeout(() => window.URL.revokeObjectURL(fileUrl), 1000);
            },
            error: () => {
              alert('Error al intentar descargar el archivo físico desde el servidor.');
            },
          });
        } else {
          // Si por alguna razón el archivo se borró del "Registro de archivos" pero quedó el comentario
          alert(`El archivo "${fileName}" ya no se encuentra en el Registro de Archivos (quizás fue eliminado).`);
        }
      },
      error: () => {
        alert('Hubo un error al intentar consultar el Registro de Archivos.');
      },
    });
  }
  // ---------------------------------------

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
