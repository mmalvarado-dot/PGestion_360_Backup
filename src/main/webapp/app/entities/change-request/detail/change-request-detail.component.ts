import { Component, inject, input, effect } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
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
            let archivosExtraidos: string[] = [];
            let comentarioLimpio = avance.comments || '';

            // Si el comentario tiene la frase clave, lo procesamos
            if (comentarioLimpio.includes('Se adjuntó el archivo:')) {
              const lineas = comentarioLimpio.split('\n');
              const lineasRestantes: string[] = [];

              lineas.forEach((linea: string) => {
                if (linea.includes('Se adjuntó el archivo:')) {
                  // Recortamos la frase y nos quedamos solo con el nombre
                  const nombreArchivo = linea.replace('Se adjuntó el archivo:', '').trim();
                  if (nombreArchivo) {
                    archivosExtraidos.push(nombreArchivo);
                  }
                } else {
                  // Si la línea no es de un archivo, la guardamos como comentario normal
                  lineasRestantes.push(linea);
                }
              });
              // Volvemos a unir el comentario sin los textos de archivos
              comentarioLimpio = lineasRestantes.join('\n').trim();
            }

            // Guardamos estas nuevas variables en el avance para usarlas en el HTML
            avance.archivosVisuales = archivosExtraidos;
            avance.comentarioVisual = comentarioLimpio;
            // ===============================================

            return avance;
          })
          .sort((a, b) => {
            // ORDEN INFALIBLE: Por ID de mayor a menor (más nuevo arriba)
            // Ignoramos la fecha para que no haya empates.
            return (b.id ?? 0) - (a.id ?? 0);
          });
      },
      error: error => {
        console.error('Error al cargar el historial de avances:', error);
      },
    });
  }

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
