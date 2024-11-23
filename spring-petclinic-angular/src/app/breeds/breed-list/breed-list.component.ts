import {Component, OnInit} from '@angular/core';
import {Breed} from '../breed';
import {Router} from '@angular/router';
import {BreedService} from '../breed.service';
import {Specialty} from '../../specialties/specialty';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-breed-list',
  templateUrl: './breed-list.component.html',
  styleUrls: ['./breed-list.component.css']
})
export class BreedListComponent implements OnInit {
  breeds: Breed[];
  errorMessage: string;
  responseStatus: number;
  isBreedsDataReceived: boolean = false;
  isInsert = false;

  constructor(private breedService: BreedService, private router: Router) {
    this.breeds = [] as Breed[];
  }

  ngOnInit() {
    this.breedService.getBreeds().pipe(
      finalize(() => {
        this.isBreedsDataReceived = true;
      })
    ).subscribe(
      breeds => this.breeds = breeds,
      error => this.errorMessage = error as any
      );
  }

  deleteBreed(breed: Breed) {
    this.breedService.deleteBreed(breed.id.toString()).subscribe(
      response => {
        this.responseStatus = response;
        this.breeds = this.breeds.filter(currentItem => !(currentItem.id === breed.id));
      },
      error => this.errorMessage = error as any);
  }

  onNewBreed(newBreed: Specialty) {
    this.breeds.push(newBreed);
    this.showAddBreedComponent();
  }

  showAddBreedComponent() {
    this.isInsert = !this.isInsert;
  }

  showEditBreedComponent(updatedBreed: Breed) {
    this.router.navigate(['/breeds', updatedBreed.id.toString(), 'edit']);
  }

  gotoHome() {
    this.router.navigate(['/welcome']);
  }
}
