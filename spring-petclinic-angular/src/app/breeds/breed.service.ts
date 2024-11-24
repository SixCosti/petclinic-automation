import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import {Breed} from './breed';
import {HttpClient} from '@angular/common/http';
import {catchError} from 'rxjs/operators';
import {HandleError, HttpErrorHandler} from '../error.service';

@Injectable()
export class BreedService {

  entityUrl = environment.REST_API_URL + 'breeds';

  private readonly handlerError: HandleError;

  constructor(private http: HttpClient, private httpErrorHandler: HttpErrorHandler) {
    this.handlerError = httpErrorHandler.createHandleError('OwnerService');
  }

  getBreeds(): Observable<Breed[]> {
    return this.http.get<Breed[]>(this.entityUrl)
      .pipe(
        catchError(this.handlerError('getBreeds', []))
      );
  }

  getBreedById(typeId: string): Observable<Breed> {
    return this.http.get<Breed>((this.entityUrl + '/' + typeId))
      .pipe(
        catchError(this.handlerError('getBreedById', {} as Breed))
      );
  }

  updateBreed(typeId: string, petType: Breed): Observable<Breed> {
    return this.http.put<Breed>(this.entityUrl + '/' + typeId, petType)
      .pipe(
        catchError(this.handlerError('updateBreed', petType))
      );
  }

  addBreed(petType: Breed): Observable<Breed> {
    return this.http.post<Breed>(this.entityUrl, petType)
      .pipe(
        catchError(this.handlerError('addBreed', petType))
      );
  }

  deleteBreed(typeId: string): Observable<number> {
    return this.http.delete<number>(this.entityUrl + '/' + typeId)
      .pipe(
        catchError(this.handlerError('deleteBreed', 0))
      );
  }

}
