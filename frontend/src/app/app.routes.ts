import { Routes } from '@angular/router';

import { AccueilComponent } from './pages/accueil/accueil.component';
import { BailleurLayoutComponent } from './pages/bailleur/bailleur-layout.component';
import { DashboardComponent } from './pages/bailleur/dashboard/dashboard.component';
import { BiensComponent } from './pages/bailleur/biens/biens.component';
import { BiensDetailComponent } from './pages/bailleur/bien-detail/bien-detail.component';
import { LocatairesComponent } from './pages/bailleur/locataires/locataires.component';
import { PaiementsComponent } from './pages/bailleur/paiements/paiements.component';
import { DocumentsComponent } from './pages/bailleur/documents/documents.component';
import { LocataireComponent } from './pages/locataire/locataire.component';
import { RechercheComponent } from './pages/recherche/recherche.component';
import { ProfilComponent } from './pages/profil/profil.component';

export const routes: Routes = [
  { path: '', component: AccueilComponent },
  {
    path: 'bailleur',
    component: BailleurLayoutComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'biens', component: BiensComponent },
      { path: 'biens/:id', component: BiensDetailComponent },
      { path: 'locataires', component: LocatairesComponent },
      { path: 'paiements', component: PaiementsComponent },
      { path: 'documents', component: DocumentsComponent },
    ]
  },
  { path: 'locataire', component: LocataireComponent },
  { path: 'recherche', component: RechercheComponent },
  { path: 'profil', component: ProfilComponent },
  { path: '**', redirectTo: '' }
];
