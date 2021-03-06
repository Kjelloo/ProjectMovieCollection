package ProjectMovieCollection.bll;

import ProjectMovieCollection.be.Category;
import ProjectMovieCollection.be.Movie;
import ProjectMovieCollection.dal.MovieData.IMovieInfoProvider;
import ProjectMovieCollection.dal.MovieData.MovieDBProvider;
import ProjectMovieCollection.dal.CategoryDBRepository;
import ProjectMovieCollection.dal.ICategoryRepository;
import ProjectMovieCollection.dal.IMovieRepository;
import ProjectMovieCollection.dal.MovieDBRepository;
import ProjectMovieCollection.utils.exception.CategoryDAOException;
import ProjectMovieCollection.utils.exception.MovieDAOException;
import ProjectMovieCollection.utils.exception.MovieInfoException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CategoryManager {

    private List<Category> categories = new ArrayList<>();
    private IMovieInfoProvider infoProvider;
    private ICategoryRepository categoryRepository;
    private IMovieRepository movieRepository;


    public CategoryManager() throws CategoryDAOException {
        //TODO: Throw to UI
        try {
            infoProvider = new MovieDBProvider();
            categoryRepository = new CategoryDBRepository();
            movieRepository = new MovieDBRepository();
        } catch (IOException e) {
            e.printStackTrace();
        }

        categories.addAll(categoryRepository.getAll());
        Category allCategory = new Category(-1, "All");
        categories.add(0, allCategory);
    }

    /**
     * Loads categories of a list of movies, from a data provider
     * @param movies
     * @throws CategoryDAOException
     * @throws MovieDAOException
     */
    public void loadCategoriesFromMovieList(List<Movie> movies) throws CategoryDAOException, MovieDAOException, MovieInfoException {
        for (Movie m : movies) {
            if (m.getCategories().size() == 0) {
                loadCategoriesFromMovie(m);
            }
            //Add all category
            m.addCategory(categories.get(0));
        }

        //Cleanup
        for (Category c : categories) {
            boolean hasMovies = false;
            for (Movie m : movies) {
                if (m.getCategories().contains(c)) {
                    hasMovies = true;
                }
            }

            if (!hasMovies) {
                categoryRepository.delete(c);
                categories.remove(c);
            }
        }
    }

    /**
     * Will load categories from a data provider
     * @param m The movie to load categories for
     * @throws CategoryDAOException If there is an error creating new categories on the data storage
     * @throws MovieDAOException If there is an error adding a new category to the movie in the data storage
     */
    private void loadCategoriesFromMovie(Movie m) throws CategoryDAOException, MovieDAOException, MovieInfoException {
        List<String> categoryStrings = new ArrayList<>();
        m.addCategory(categories.get(0));

        if (m.getProviderID() != -1) {
            categoryStrings.addAll(infoProvider.getCategories(m.getProviderID()));
        }

        for (String category : categoryStrings) {
            Category categoryToAdd = null;
            for (Category c : categories) {
                if (c.getName().equals(category)) {
                    categoryToAdd = c;
                }
            }
            if (categoryToAdd == null) {
                Category tempCategory = new Category(-1, category);
                categoryToAdd = categoryRepository.create(tempCategory);
                categories.add(categoryToAdd);
            }

            movieRepository.addCategoryToMovie(m, categoryToAdd);
            m.addCategory(categoryToAdd);
        }
    }

    public List<Category> getAllCategories() {
        return categories;
    }

}
