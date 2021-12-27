package com.epam.esm.repository;

import java.util.Optional;

import com.epam.esm.repository.model.UserModel;

/**
 * 
 * Contains methods for working mostly with {@code UserModel} entity.
 *
 */
public interface UserRepository extends GeneralRepository<UserModel> {

	/**
	 * Reads user with passed name.
	 * 
	 * @param userName the name of the user to be read
	 * @return user with passed name
	 */
	Optional<UserModel> findByLogin(String login);
	
	/**
	 * Checks whether user with passed id exists.
	 * 
	 * @param userId the id of user to be checked
	 * @return {@code true} if the the user with passed id already exists and
	 *         {@code false} otherwise
	 */
	boolean userExistsById(long userId);

}
