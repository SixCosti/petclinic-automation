import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Breed} from '../breed';
import {BreedService} from '../breed.service';

@Component({
  selector: 'app-breed-add',
  templateUrl: './breed-add.component.html',
  styleUrls: ['./breed-add.component.css']
})
export class BreedAddComponent implements OnInit {
  breed: Breed;
  errorMessage: string;
  @Output() newBreed = new EventEmitter<Breed>();

  constructor(private breedService: BreedService) {
    this.breed = {} as Breed;
  }

  ngOnInit() {
  }

  onSubmit(breed: Breed) {
    breed.id = null;
    this.breedService.addBreed(breed).subscribe(
      newBreed => {
        this.breed = newBreed;
        this.newBreed.emit(this.breed);
      },
      error => this.errorMessage = error as any
    );
  }

}
