package com.epam.esm.repository;

import java.util.List;
import java.util.Optional;

/**
 * 
 * Contains common methods for working with all entities during the interaction
 * with database.
 *
 */
public interface GeneralRepository<T> {
	/**
	 * Saves the passed entity.
	 * 
	 * @param model the entity to be saved
	 * @return saved entity
	 */
	T save(T model);

	/**
	 * Reads entity with passed id.
	 * 
	 * @param id the id of the entity to be read
	 * @return entity with passed id
	 */
	Optional<T> findById(long id);

	/**
	 * Reads all entities according to the passed parameters.
	 * 
	 * @param offset start position for entities reading
	 * @param limit  amount of entities to be read
	 * @return entities which meet passed parameters
	 */
	List<T> findAll(int offset, int limit);
}
