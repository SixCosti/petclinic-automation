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

package org.springframework.samples.petclinic.repository.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.orm.ObjectRetrievalFailureException;
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
@Profile("jdbc")
public class JdbcBreedRepositoryImpl implements BreedRepository {

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private SimpleJdbcInsert insertBreed;

	@Autowired
	public JdbcBreedRepositoryImpl(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.insertBreed = new SimpleJdbcInsert(dataSource)
	            .withTableName("types")
	            .usingGeneratedKeyColumns("id");
	}

	@Override
	public Breed findById(int id) {
		Breed breed;
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            breed = this.namedParameterJdbcTemplate.queryForObject(
                "SELECT id, name FROM types WHERE id= :id",
                params,
                BeanPropertyRowMapper.newInstance(Breed.class));
        } catch (EmptyResultDataAccessException ex) {
            throw new ObjectRetrievalFailureException(Breed.class, id);
        }
        return breed;
	}

    @Override
    public Breed findByName(String name) throws DataAccessException {
        Breed breed;
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("name", name);
            breed = this.namedParameterJdbcTemplate.queryForObject(
                "SELECT id, name FROM types WHERE name= :name",
                params,
                BeanPropertyRowMapper.newInstance(Breed.class));
        } catch (EmptyResultDataAccessException ex) {
            throw new ObjectRetrievalFailureException(Breed.class, name);
        }
        return breed;
    }

    @Override
	public Collection<Breed> findAll() throws DataAccessException {
		Map<String, Object> params = new HashMap<>();
        return this.namedParameterJdbcTemplate.query(
            "SELECT id, name FROM types",
            params,
            BeanPropertyRowMapper.newInstance(Breed.class));
	}

	@Override
	public void save(Breed breed) throws DataAccessException {
		BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(breed);
		if (breed.isNew()) {
            Number newKey = this.insertBreed.executeAndReturnKey(parameterSource);
            breed.setId(newKey.intValue());
        } else {
            this.namedParameterJdbcTemplate.update("UPDATE types SET name=:name WHERE id=:id",
                parameterSource);
        }
	}

	@Override
	public void delete(Breed breed) throws DataAccessException {
		Map<String, Object> pettype_params = new HashMap<>();
		pettype_params.put("id", breed.getId());
		List<Pet> pets = new ArrayList<Pet>();
		pets = this.namedParameterJdbcTemplate.
    			query("SELECT pets.id, name, birth_date, type_id, owner_id FROM pets WHERE type_id=:id",
    			pettype_params,
    			BeanPropertyRowMapper.newInstance(Pet.class));
		// cascade delete pets
		for (Pet pet : pets){
			Map<String, Object> pet_params = new HashMap<>();
			pet_params.put("id", pet.getId());
			List<Visit> visits = new ArrayList<Visit>();
			visits = this.namedParameterJdbcTemplate.query(
		            "SELECT id, pet_id, visit_date, description FROM visits WHERE pet_id = :id",
		            pet_params,
		            BeanPropertyRowMapper.newInstance(Visit.class));
	        // cascade delete visits
	        for (Visit visit : visits){
	        	Map<String, Object> visit_params = new HashMap<>();
	        	visit_params.put("id", visit.getId());
	        	this.namedParameterJdbcTemplate.update("DELETE FROM visits WHERE id=:id", visit_params);
	        }
	        this.namedParameterJdbcTemplate.update("DELETE FROM pets WHERE id=:id", pet_params);
        }
        this.namedParameterJdbcTemplate.update("DELETE FROM types WHERE id=:id", pettype_params);
	}

}
