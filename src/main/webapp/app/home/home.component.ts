import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
// 1. Importamos el componente de iconos de FontAwesome
import { FaIconComponent } from '@fortawesome/angular-fontawesome';

import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';

@Component({
  standalone: true,
  selector: 'jhi-home',
  // 2. Agregamos FaIconComponent al arreglo de imports para que el HTML lo reconozca
  imports: [RouterModule, FaIconComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'], // <-- Aquí está la conexión con  estilos
})
export default class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  private readonly destroy$ = new Subject<void>();

  private accountService = inject(AccountService);
  private router = inject(Router);

  ngOnInit(): void {
    // 1. Verificamos la sesión apenas el usuario entra a la ruta principal ('/')
    this.accountService.identity().subscribe(account => {
      this.account = account;
      if (account === null) {
        // Si no está logueado, lo pateamos a la pantalla de login
        this.router.navigate(['/login']);
      }
    });

    // 2. Escuchamos los cambios (para cuando cierras sesión)
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => {
        this.account = account;
        if (account === null) {
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 50);
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
