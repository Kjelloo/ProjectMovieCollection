/**
 * @author kjell
 */

package ProjectMovieCollection.dal;

import ProjectMovieCollection.be.Category;
import ProjectMovieCollection.be.Movie;
import ProjectMovieCollection.utils.db.DBConnector;
import ProjectMovieCollection.utils.exception.MovieDAOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDBRepository implements IMovieRepository {

    private DBConnector dbConnector;

    public MovieDBRepository() {
        dbConnector = new DBConnector();
    }

    @Override
    public Movie create(Movie movie) throws MovieDAOException {

        try (Connection connection = dbConnector.getConnection()) {
            String sql = "INSERT INTO Movies (title, rating, filepath, lastview, imgPath, providerID, description) VALUES (?,?,?,?,?,?,?);";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            makePreparedStatement(movie, statement);
            statement.execute();

            try(ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    Movie m = new Movie(keys.getInt(1), movie.getTitle(), movie.getFilepath());
                    m.setImgPath(movie.getImgPath());
                    m.setProviderID(movie.getProviderID());
                    m.setDesc(movie.getDesc());
                    return m;
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
            String sql = "UPDATE Movies SET title=?, rating=?, filepath=?, lastview=?, imgPath=?, providerID=?, description=? WHERE id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            makePreparedStatement(movie, statement);
            statement.setInt(8, movie.getId());
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
                    String imgPath = resultSet.getString("imgPath");
                    int providerID = resultSet.getInt("providerID");
                    String desc = resultSet.getString("description");
                    int rating = resultSet.getInt("rating");

                    Movie movie = new Movie(id, title, filepath);
                    movie.setImgPath(imgPath);
                    movie.setProviderID(providerID);
                    movie.setDesc(desc);
                    movie.setCategories(getCategories(movie));
                    movie.setRating(rating);
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
            String sql = "SELECT Categories.id, name FROM CatMovie INNER JOIN Categories ON categoryID=Categories.id WHERE movieID=?;";
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

    @Override
    public void addCategoryToMovie(Movie m, Category c) throws MovieDAOException {
        try (Connection connection = dbConnector.getConnection()) {
            String sql = "INSERT INTO CatMovie (movieID, categoryID) VALUES (?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, m.getId());
            statement.setInt(2, c.getId());
            statement.execute();
        } catch (SQLException e) {
            throw new MovieDAOException("Could not add category to movie", e);
        }
    }


    private void makePreparedStatement(Movie movie, PreparedStatement statement) throws SQLException {
        statement.setString(1, movie.getTitle());
        statement.setInt(2, movie.getRating());
        statement.setString(3, movie.getFilepath());
        statement.setDate(4, new Date(movie.getLastView().getTime()));
        statement.setString(5, movie.getImgPath());
        statement.setInt(6, movie.getProviderID());
        statement.setString(7, movie.getDesc());
    }

}
