import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * Minimal CoreUIModule placeholder.
 *
 * The original CoreUI demo module imported many symbols like `CAlertModule`,
 * `CBadgeModule`, etc. from `@coreui/angular` that do not exist in the
 * version you are using (`@coreui/angular` ^4.8.0). That caused a large
 * number of TypeScript errors.
 *
 * This simplified module keeps the name `CoreUIModule` so that any existing
 * imports remain valid, but it no longer imports any invalid CoreUI symbols.
 * If you later decide to integrate CoreUI components into the main app,
 * you can extend the `imports`/`exports` arrays here with the correct
 * modules from the official CoreUI Angular documentation.
 */
@NgModule({
  imports: [CommonModule],
  exports: [CommonModule]
})
export class CoreUIModule {}

