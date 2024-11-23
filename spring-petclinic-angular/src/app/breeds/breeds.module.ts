import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {BreedService} from './breed.service';
import {BreedListComponent} from './breed-list/breed-list.component';
import {BreedAddComponent} from './breed-add/breed-add.component';
import {BreedEditComponent} from './breed-edit/breed-edit.component';
import {BreedsRoutingModule} from './breeds-routing.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    BreedsRoutingModule
  ],
  declarations: [
    BreedListComponent,
    BreedAddComponent,
    BreedEditComponent],
  exports: [
    BreedListComponent
  ],
  providers: [BreedService]
})
export class BreedsModule {
}
