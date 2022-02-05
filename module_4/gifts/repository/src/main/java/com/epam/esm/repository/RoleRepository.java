package com.epam.esm.repository;

import java.util.Optional;

import com.epam.esm.repository.model.RoleModel;

/**
 * 
 * Contains methods for working mostly with {@code RoleModel} entity.
 *
 */
public interface RoleRepository {
	/**
	 * Reads role with passed name.
	 * 
	 * @param name the name of the role to be read
	 * @return role with passed name
	 */
	Optional<RoleModel> findByName(String name);
}
