/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.rest.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.mapper.BreedMapper;
import org.springframework.samples.petclinic.model.Breed;
import org.springframework.samples.petclinic.rest.api.BreedsApi;
import org.springframework.samples.petclinic.rest.dto.BreedDto;
import org.springframework.samples.petclinic.rest.dto.BreedFieldsDto;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api")
public class BreedRestController implements BreedsApi {

    private final ClinicService clinicService;
    private final BreedMapper breedMapper;


    public BreedRestController(ClinicService clinicService, BreedMapper breedMapper) {
        this.clinicService = clinicService;
        this.breedMapper = breedMapper;
    }

    @PreAuthorize("hasAnyRole(@roles.OWNER_ADMIN, @roles.VET_ADMIN)")
    @Override
    public ResponseEntity<List<BreedDto>> listBreeds() {
        List<Breed> breeds = new ArrayList<>(this.clinicService.findAllBreeds());
        if (breeds.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(breedMapper.toBreedDtos(breeds), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole(@roles.OWNER_ADMIN, @roles.VET_ADMIN)")
    @Override
    public ResponseEntity<BreedDto> getBreed(Integer breedId) {
        Breed breed = this.clinicService.findBreedById(breedId);
        if (breed == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(breedMapper.toBreedDto(breed), HttpStatus.OK);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<BreedDto> addBreed(BreedFieldsDto breedFieldsDto) {
        HttpHeaders headers = new HttpHeaders();
        final Breed breed = breedMapper.toBreed(breedFieldsDto);
        this.clinicService.saveBreed(breed);
        headers.setLocation(UriComponentsBuilder.newInstance().path("/api/breeds/{id}").buildAndExpand(breed.getId()).toUri());
        return new ResponseEntity<>(breedMapper.toBreedDto(breed), headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Override
    public ResponseEntity<BreedDto> updateBreed(Integer breedId, BreedDto breedDto) {
        Breed currentBreed = this.clinicService.findBreedById(breedId);
        if (currentBreed == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        currentBreed.setName(breedDto.getName());
        this.clinicService.saveBreed(currentBreed);
        return new ResponseEntity<>(breedMapper.toBreedDto(currentBreed), HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole(@roles.VET_ADMIN)")
    @Transactional
    @Override
    public ResponseEntity<BreedDto> deleteBreed(Integer breedId) {
        Breed breed = this.clinicService.findBreedById(breedId);
        if (breed == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        this.clinicService.deleteBreed(breed);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
