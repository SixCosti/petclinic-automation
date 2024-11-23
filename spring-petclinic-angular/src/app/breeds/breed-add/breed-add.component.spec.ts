import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {BreedAddComponent} from './breed-add.component';
import {BreedService} from '../breed.service';
import {Breed} from '../breed';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ActivatedRouteStub, RouterStub} from '../../testing/router-stubs';
import {FormsModule} from '@angular/forms';
import {Observable, of} from 'rxjs';
import Spy = jasmine.Spy;

class BreedServiceStub {
  addBreed(petType: Breed): Observable<Breed> {
    return of();
  }
}

describe('BreedAddComponent', () => {
  let component: BreedAddComponent;
  let fixture: ComponentFixture<BreedAddComponent>;
  let breedService: BreedService;
  let spy: Spy;
  let testBreed: Breed;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ BreedAddComponent ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      imports: [FormsModule],
      providers: [
        {provide: BreedService, useClass: BreedServiceStub},
        {provide: Router, useClass: RouterStub},
        {provide: ActivatedRoute, useClass: ActivatedRouteStub}
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BreedAddComponent);
    component = fixture.componentInstance;
    testBreed = {
      id: 1,
      name: 'test'
    };

    breedService = fixture.debugElement.injector.get(BreedService);
    spy = spyOn(breedService, 'addBreed')
      .and.returnValue(of(testBreed));

    fixture.detectChanges();
  });

  it('should create BreedAddComponent', () => {
    expect(component).toBeTruthy();
  });
});
