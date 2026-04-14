import { Component } from '@angular/core';

import { SiteLayoutComponent } from './layout/layout.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [SiteLayoutComponent],
  template: '<app-site-layout />'
})
export class AppComponent {}