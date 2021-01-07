/**
 * @author kjell
 */

package ProjectMovieCollection.dal.dao;

import ProjectMovieCollection.be.Category;
import ProjectMovieCollection.be.Movie;
import ProjectMovieCollection.dal.dao.interfaces.MovieRepository;
import ProjectMovieCollection.utils.db.DBConnector;
import ProjectMovieCollection.utils.exception.MovieDAOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO implements MovieRepository {

    private DBConnector dbConnector;

    public MovieDAO() {
        dbConnector = new DBConnector();
    }

    @Override
    public Movie create(Movie movie) throws MovieDAOException {

        try (Connection connection = dbConnector.getConnection()) {
            String sql = "INSERT INTO Movies (title, rating, filepath, lastview) VALUES (?,?,?,?);";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, movie.getTitle());
            statement.setInt(2, movie.getRating());
            statement.setString(3, movie.getFilepath());
            statement.setDate(4, (Date) movie.getLastView());
            statement.execute();

            try(ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Movie(keys.getInt(1), movie.getTitle(), movie.getFilepath());
                }
            }
            throw new MovieDAOException("Could not create movie");

        } catch (SQLException e) {
            throw new MovieDAOException("Could not connect to database", e);
        }
    }

    @Override
    public void delete(Movie movie) throws MovieDAOException {

        try(Connection connection = dbConnector.getConnection()) {
            String sql = "DELETE FROM Movies WHERE id=?;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, movie.getId());
            statement.execute();
        } catch (SQLException e) {
            throw new MovieDAOException("Could not connect to database", e);
        }
    }

    @Override
    public void update(Movie movie) throws MovieDAOException {

        try(Connection connection = dbConnector.getConnection()) {
            String sql = "UPDATE Movies SET title=?, rating=?, filepath=?, lastview=?, WHERE id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, movie.getTitle());
            statement.setInt(2, movie.getRating());
            statement.setString(3, movie.getFilepath());
            statement.setDate(4, (Date) movie.getLastView());
            statement.setInt(5, movie.getId());
            statement.execute();

        } catch (SQLException e) {
            throw new MovieDAOException("Could not connect to database", e);
        }
    }

    @Override
    public List<Movie> getAll() throws MovieDAOException {

        ArrayList<Movie> allMovies = new ArrayList<>();

        try (Connection connection = dbConnector.getConnection()) {
            String sql = "SELECT * FROM Movies;";
            Statement statement = connection.createStatement();

            if (statement.execute(sql)) {
                ResultSet resultSet = statement.getResultSet();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String title = resultSet.getString("title");
                    String filepath = resultSet.getString("filepath");

                    Movie movie = new Movie(id, title, filepath);
                    allMovies.add(movie);
                }
            }

        } catch (SQLException e) {
            throw new MovieDAOException("Could not connect to database", e);
        }

        return allMovies;
    }

    @Override
    public List<Category> getCategories(Movie movie) throws MovieDAOException {

        List<Category> categoryList = new ArrayList<>();

        try (Connection connection = dbConnector.getConnection()) {
            String sql = "SELECT id, name FROM CatMovie INNER JOIN Categories ON categoryID=Categories.id WHERE movieID=?;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, movie.getId());

            if (statement.execute()) {
                ResultSet resultSet = statement.getResultSet();
                while (resultSet.next()) {
                    Category category = new Category(resultSet.getInt("id"), resultSet.getString("name"));

                    categoryList.add(category);
                }
            }
        } catch (SQLException e) {
            throw new MovieDAOException("Could not load all movies from category", e);
        }

        return categoryList;
    }

}
