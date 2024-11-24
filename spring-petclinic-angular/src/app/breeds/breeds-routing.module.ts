import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {BreedListComponent} from './breed-list/breed-list.component';
import {BreedAddComponent} from './breed-add/breed-add.component';
import {BreedEditComponent} from './breed-edit/breed-edit.component';

const breedsRoutes: Routes = [
  {path: 'breeds', component: BreedListComponent},
  {path: 'breeds/add', component: BreedAddComponent},
  {path: 'breeds/:id/edit', component: BreedEditComponent}
];

@NgModule({
  imports: [RouterModule.forChild(breedsRoutes)],
  exports: [RouterModule]
})

export class BreedsRoutingModule {
}
