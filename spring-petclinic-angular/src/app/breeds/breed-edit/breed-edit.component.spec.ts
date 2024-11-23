import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {BreedEditComponent} from './breed-edit.component';
import {BreedService} from '../breed.service';
import {Breed} from '../breed';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ActivatedRouteStub, RouterStub} from '../../testing/router-stubs';
import {FormsModule} from '@angular/forms';
import {Observable, of} from 'rxjs/index';
import Spy = jasmine.Spy;

class BreedServiceStub {
  getBreedById(typeId: string): Observable<Breed> {
    return of();
  }
}


describe('BreedEditComponent', () => {
  let component: BreedEditComponent;
  let fixture: ComponentFixture<BreedEditComponent>;
  let breedService: BreedService;
  let spy: Spy;
  let testBreed: Breed;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ BreedEditComponent ],
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
    fixture = TestBed.createComponent(BreedEditComponent);
    component = fixture.componentInstance;
    testBreed = {
      id: 1,
      name: 'test'
    };

    breedService = fixture.debugElement.injector.get(BreedService);
    spy = spyOn(breedService, 'getBreedById')
      .and.returnValue(of(testBreed));

    fixture.detectChanges();
  });

  it('should create BreedEditComponent', () => {
    expect(component).toBeTruthy();
  });
});
