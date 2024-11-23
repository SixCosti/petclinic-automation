/*
 *
 *  * Copyright 2016-2017 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

/**
 * @author Vitaliy Fedoriv
 */

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
