import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {BreedListComponent} from './breed-list.component';
import {BreedService} from '../breed.service';
import {Breed} from '../breed';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ActivatedRouteStub, RouterStub} from '../../testing/router-stubs';
import {FormsModule} from '@angular/forms';
import {Observable, of} from 'rxjs/index';
import Spy = jasmine.Spy;

class BreedServiceStub {
  deleteBreed(typeId: string): Observable<number> {
    return of();
  }
  getBreeds(): Observable<Breed[]> {
    return of();
  }
}


describe('BreedListComponent', () => {
  let component: BreedListComponent;
  let fixture: ComponentFixture<BreedListComponent>;
  let breedService: BreedService;
  let spy: Spy;
  let testBreeds: Breed[];
  let responseStatus: number;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ BreedListComponent ],
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
    fixture = TestBed.createComponent(BreedListComponent);
    component = fixture.componentInstance;

    testBreeds = [{
      id: 1,
      name: 'test'
    }];

    breedService = fixture.debugElement.injector.get(BreedService);
    responseStatus = 204; // success delete return NO_CONTENT
    component.breeds = testBreeds;

    spy = spyOn(breedService, 'deleteBreed')
      .and.returnValue(of(responseStatus));

    fixture.detectChanges();
  });

  it('should create BreedListComponent', () => {
    expect(component).toBeTruthy();
  });

  it('should call deleteBreed() method', () => {
    fixture.detectChanges();
    component.deleteBreed(component.breeds[0]);
    expect(spy.calls.any()).toBe(true, 'deleteBreed called');
  });
});
