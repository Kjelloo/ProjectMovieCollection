package ProjectMovieCollection.dal.dao.interfaces;

import ProjectMovieCollection.be.Category;
import ProjectMovieCollection.be.Movie;
import ProjectMovieCollection.utils.exception.MovieDAOException;

import java.util.List;

public interface MovieRepository {

    /**
     * Creates movie
     * @param movie The movie to be created
     * @return The movie object
     * @throws MovieDAOException
     */
    Movie create(Movie movie) throws MovieDAOException;

    /**
     * Deletes movie
     * @param movie The movie to be deleted
     * @throws MovieDAOException
     */
    void delete(Movie movie) throws MovieDAOException;

    /**
     * Updates movie
     * @param movie movie to update
     * @throws MovieDAOException
     */
    void update(Movie movie) throws MovieDAOException;

    /**
     * Get all movies
     * @return list of all movies
     * @throws MovieDAOException
     */
    List<Movie> getAll() throws MovieDAOException;

    /**
     * Get all categories for a movie
     * @param movie The movie to get the categories from
     * @return List of categories
     * @throws MovieDAOException
     */
    List<Category> getCategories(Movie movie) throws MovieDAOException;

}
