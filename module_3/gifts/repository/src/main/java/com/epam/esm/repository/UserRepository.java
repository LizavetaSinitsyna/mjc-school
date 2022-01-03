package com.epam.esm.repository;

import java.util.List;
import java.util.Optional;

import com.epam.esm.repository.model.UserModel;

/**
 * 
 * Contains methods for working mostly with {@code UserModel} entity.
 *
 */
public interface UserRepository extends GeneralRepository<UserModel> {
	/**
	 * Reads user with passed login.
	 * 
	 * @param login the name of the user to be read
	 * @return user with passed login
	 */
	Optional<UserModel> findByLogin(String login);

	/**
	 * Checks whether the user with passed id exists.
	 * 
	 * @param userId the id of user to be checked
	 * @return {@code true} if the the user with passed id already exists and
	 *         {@code false} otherwise
	 */
	boolean userExistsById(long userId);

	/**
	 * Checks whether user with passed login exists.
	 * 
	 * @param login the login of the user to be checked
	 * @return {@code true} if the the user with passed login already exists and
	 *         {@code false} otherwise
	 */
	boolean userExistsByLogin(String login);

	/**
	 * Saves the passed users.
	 * 
	 * @param userModels the users to be saved
	 * @return saved users
	 */
	List<UserModel> saveUsers(List<UserModel> userModels);
}
