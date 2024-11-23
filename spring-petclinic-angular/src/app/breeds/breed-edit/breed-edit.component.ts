import {Component, OnInit} from '@angular/core';
import {Breed} from '../breed';
import {BreedService} from '../breed.service';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-breed-edit',
  templateUrl: './breed-edit.component.html',
  styleUrls: ['./breed-edit.component.css']
})
export class BreedEditComponent implements OnInit {
  breed: Breed;
  errorMessage: string;

  constructor(private breedService: BreedService, private route: ActivatedRoute, private router: Router) {
    this.breed = {} as Breed;
  }

  ngOnInit() {
    const breedId = this.route.snapshot.params.id;
    this.breedService.getBreedById(breedId).subscribe(
      breed => this.breed = breed,
      error => this.errorMessage = error as any);
  }

  onSubmit(breed: Breed) {
    this.breedService.updateBreed(breed.id.toString(), breed).subscribe(
      res => {
        console.log('update success');
        this.onBack();
      },
      error => this.errorMessage = error as any);

  }

  onBack() {
    this.router.navigate(['/breeds']);
  }

}
