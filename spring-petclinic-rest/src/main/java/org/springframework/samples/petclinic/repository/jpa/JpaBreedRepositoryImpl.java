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

package org.springframework.samples.petclinic.repository.jpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Breed;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.BreedRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Vitaliy Fedoriv
 *
 */

@Repository
@Profile("jpa")
public class JpaBreedRepositoryImpl implements BreedRepository {

    @PersistenceContext
    private EntityManager em;

	@Override
	public Breed findById(int id) {
		return this.em.find(Breed.class, id);
	}

    @Override
    public Breed findByName(String name) throws DataAccessException {
        return this.em.createQuery("SELECT p FROM Breed p WHERE p.name = :name", Breed.class)
            .setParameter("name", name)
            .getSingleResult();
    }


    @SuppressWarnings("unchecked")
	@Override
	public Collection<Breed> findAll() throws DataAccessException {
		return this.em.createQuery("SELECT pbreed FROM Breed pbreed").getResultList();
	}

	@Override
	public void save(Breed breed) throws DataAccessException {
		if (breed.getId() == null) {
            this.em.persist(breed);
        } else {
            this.em.merge(breed);
        }

	}

	@SuppressWarnings("unchecked")
	@Override
	public void delete(Breed breed) throws DataAccessException {
		this.em.remove(this.em.contains(breed) ? breed : this.em.merge(breed));
		Integer breedId = breed.getId();

		List<Pet> pets = this.em.createQuery("SELECT pet FROM Pet pet WHERE breed.id=" + breedId).getResultList();
		for (Pet pet : pets){
			List<Visit> visits = pet.getVisits();
			for (Visit visit : visits){
				this.em.createQuery("DELETE FROM Visit visit WHERE id=" + visit.getId()).executeUpdate();
			}
			this.em.createQuery("DELETE FROM Pet pet WHERE id=" + pet.getId()).executeUpdate();
		}
		this.em.createQuery("DELETE FROM Breed breed WHERE id=" + breedId).executeUpdate();
	}

}
