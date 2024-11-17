package org.springframework.samples.petclinic.mapper;

import org.mapstruct.Mapper;
import org.springframework.samples.petclinic.rest.dto.BreedDto;
import org.springframework.samples.petclinic.model.Breed;
import org.springframework.samples.petclinic.rest.dto.BreedFieldsDto;

import java.util.Collection;
import java.util.List;

/**
 * Map Breed & BreedDto using mapstruct
 */
@Mapper
public interface BreedMapper {

    Breed toBreed(BreedDto breedDto);

    Breed toBreed(BreedFieldsDto breedFieldsDto);

    BreedDto toBreedDto(Breed breed);
    BreedFieldsDto toBreedFieldsDto(Breed breed);

    List<BreedDto> toBreedDtos(Collection<Breed> breeds);
}
