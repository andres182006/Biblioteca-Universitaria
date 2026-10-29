import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { Prestamo, PrestamoService } from '../../services/prestamo.service';

type Tab = 'pendientes' | 'aprobadas' | 'rechazadas';
interface Toast { id: number; mensaje: string; tipo: 'ok' | 'error' | 'info'; }

@Component({
  imports: [DatePipe, RouterLink, RouterLinkActive],
  selector: 'app-prestamos',
  styleUrl: './prestamos.css',
  templateUrl: './prestamos.html',
})
export class Prestamos implements OnInit{
  private service = inject(PrestamoService);
  private router = inject(Router);

  todas = signal<Prestamo[]>([]);
  cargando = signal(true);
  errorCarga = signal('');
  procesando = signal(false);
  tab = signal<Tab>('pendientes');
  busqueda = signal('');

  aAprobar = signal<Prestamo | null>(null);
  aRechazar = signal<Prestamo | null>(null);
  motivo = signal('');
  errorModal = signal('');
  toasts = signal<Toast[]>([]);
  private siguienteToast = 0;

  correo = localStorage.getItem('correo') ?? '';
  esAdmin = (JSON.parse(localStorage.getItem('roles') ?? '[]') as string[])
    .includes('ROLE_ADMINISTRADOR');

  pendientes = computed(() =>
    this.todas()
      .filter((p) => p.estado === 'solicitado')
      .sort((a, b) => a.fechasolicitud.localeCompare(b.fechasolicitud))
  );
  aprobadas = computed(() =>
    this.todas().filter((p) => p.estado === 'activo' || p.estado === 'devuelto')
  );
  rechazadas = computed(() => this.todas().filter((p) => p.estado === 'rechazado'));

  aprobadasHoy = computed(() => {
    const hoy = new Date().toLocaleDateString('sv-SE'); // yyyy-MM-dd
    return this.aprobadas().filter((p) => p.fechaprestamo === hoy).length;
  });
  prestamosActivos = computed(() => this.todas().filter((p) => p.estado === 'activo').length);
  sinCopias = computed(() => this.pendientes().filter((p) => this.sinExistencias(p)).length);

  visibles = computed(() => {
    const t = this.tab();
    const base = t === 'pendientes' ? this.pendientes() : t === 'aprobadas' ? this.aprobadas() : this.rechazadas();
    const q = this.normalizar(this.busqueda());
    if (!q) return base;
    return base.filter((p) =>
      this.normalizar(
        [p.estudiante?.nombrecompleto, p.estudiante?.documento, p.libro?.titulo, p.libro?.autor, p.libro?.isbn].join(' ')
      ).includes(q)
    );
  });

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    this.cargando.set(true);
    this.errorCarga.set('');
    this.service.historial().subscribe({
      next: (res) => {
        this.todas.set(res.data ?? []);
        this.cargando.set(false);
      },
      error: (err) => {
        this.cargando.set(false);
        this.errorCarga.set(this.mensajeError(err));
      }
    });
  }

  private recargar() {
    this.service.historial().subscribe({
      next: (res) => this.todas.set(res.data ?? [])
    });
  }

  // ---------- Aprobar ----------
  confirmarAprobacion() {
    const p = this.aAprobar();
    if (!p) return;
    this.procesando.set(true);
    this.service.aprobar(p.id).subscribe({
      next: () => {
        this.procesando.set(false);
        this.aAprobar.set(null);
        this.toast('Préstamo aprobado con éxito.', 'ok');
        this.recargar();
      },
      error: (err) => {
        this.procesando.set(false);
        this.aAprobar.set(null);
        this.errorModal.set(this.mensajeError(err));
        this.recargar();
      }
    });
  }

  // ---------- Rechazar ----------
  seleccionarRechazo(p: Prestamo) {
    this.aRechazar.set(p);
    this.motivo.set('');
    setTimeout(() =>
      document.getElementById('panel-rechazo')?.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
    );
  }

  cancelarRechazo() {
    this.aRechazar.set(null);
    this.motivo.set('');
  }

  confirmarRechazo() {
    const p = this.aRechazar();
    if (!p) return;
    this.procesando.set(true);
    this.service.rechazar(p.id, this.motivo().trim() || undefined).subscribe({
      next: () => {
        this.procesando.set(false);
        this.cancelarRechazo();
        this.toast('Solicitud rechazada correctamente.', 'ok');
        this.recargar();
      },
      error: (err) => {
        this.procesando.set(false);
        this.errorModal.set(this.mensajeError(err));
        this.recargar();
      }
    });
  }

  // ---------- Auxiliares de la vista ----------
  sinExistencias(p: Prestamo): boolean {
    return p.estado === 'solicitado' && (p.libro?.cantidaddisponible ?? 0) <= 0;
  }

  badge(p: Prestamo): { texto: string; clases: string } {
    switch (p.estado) {
      case 'solicitado':
        return this.sinExistencias(p)
          ? { texto: 'Sin existencias', clases: 'bg-error-container/40 text-error font-semibold' }
          : { texto: 'Pendiente', clases: 'bg-surface-container text-on-surface font-medium' };
      case 'activo':
        return { texto: 'Aprobada', clases: 'bg-surface-container-high text-primary font-semibold' };
      case 'devuelto':
        return { texto: 'Devuelta', clases: 'bg-surface-container-high text-primary font-semibold' };
      default:
        return { texto: 'Rechazada', clases: 'bg-error-container/40 text-error font-semibold' };
    }
  }

  iniciales(nombre?: string): string {
    return (nombre ?? '')
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map((n) => n[0])
      .join('')
      .toUpperCase();
  }

  vacioTitulo(): string {
    if (this.busqueda().trim()) return 'Sin resultados';
    return { pendientes: 'No hay solicitudes pendientes', aprobadas: 'Aún no hay solicitudes aprobadas', rechazadas: 'Aún no hay solicitudes rechazadas' }[this.tab()];
  }

  vacioTexto(): string {
    if (this.busqueda().trim()) return 'Prueba con otro nombre, documento o libro.';
    return this.tab() === 'pendientes'
      ? 'Las nuevas solicitudes aparecerán aquí cuando los estudiantes las envíen.'
      : 'Cuando haya movimientos en esta sección, los verás aquí.';
  }

  cerrarSesion() {
    ['token', 'correo', 'roles'].forEach((k) => localStorage.removeItem(k));
    this.router.navigate(['/login']);
  }

  toastClases(t: Toast): string {
    return t.tipo === 'ok' ? 'bg-secondary text-on-secondary' : t.tipo === 'error' ? 'bg-error text-on-error' : 'bg-primary text-on-primary';
  }

  private toast(mensaje: string, tipo: Toast['tipo']) {
    const id = ++this.siguienteToast;
    this.toasts.update((l) => [...l, { id, mensaje, tipo }]);
    setTimeout(() => this.toasts.update((l) => l.filter((t) => t.id !== id)), 4000);
  }

  private mensajeError(err: any): string {
    if (err.status === 0) return 'No se pudo conectar con el servidor. Inténtalo de nuevo.';
    if (err.status === 403) return 'Tu sesión expiró o no tienes permisos para esta acción. Vuelve a iniciar sesión.';
    return err.error?.message ?? 'Ocurrió un error inesperado.';
  }

  private normalizar(s: string): string {
    return (s ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase().trim();
  }
}

