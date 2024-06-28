package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        // get all categories
        List<Category> categories = new ArrayList<>(); // create an arrayList of all the categories
        String query = "SELECT * FROM categories"; // write sql query pulling data from data bases

        try(Connection con = getConnection(); // Connect to the database
            PreparedStatement pS = con.prepareStatement(query); // send queries to the database
            ResultSet resultSet = pS.executeQuery();){ // retrieves data from database

            while(resultSet.next()){ // parse all the rows of data
                Category category = mapRow(resultSet); // mapRow creates category
                categories.add(category); // add it to arrayList
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return categories; // return categories
    }


    @Override
    public Category getById(int categoryId)
    {
        // get category by id
        String query = "SELECT * FROM categories WHERE category_id = ?"; // write to a sql query

        try(Connection con = getConnection(); // connecting to database
            PreparedStatement pS = con.prepareStatement(query)){ // preparing the queries to send to the database
            pS.setInt(1, categoryId); // setting the parameters of query

            try(ResultSet resultSet = pS.executeQuery()) { // send queries to database
                if (resultSet.next()){ // if result has something in it
                    return mapRow(resultSet); // return category
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e); // try always has a catch
        }


        return null; // if there's nothing returns null
    }

    @Override
    public Category create(Category category)
    {
        // create a new category
        String query = "INSERT INTO categories (name, description) VALUES (?, ?)"; // write to sql query

        try(Connection con = getConnection(); // connect to database
            PreparedStatement pS = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);){ // prepares the queries to send to the database
            pS.setString(1, category.getName()); // setting parameters of sql query
            pS.setString(2, category.getDescription()); // setting parameters of sql query
            int rows = pS.executeUpdate(); // returns the number of rows affected by the update

            if (rows > 0){  // if updated a row
                ResultSet generatedKeys = pS.getGeneratedKeys(); // create resultSet of generated keys

                if (generatedKeys.next()) { // if generated keys has a value in it
                    int orderId = generatedKeys.getInt(1); // grab value and assign it to order id

                    return getById(orderId); // return getById(orderId)
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        // update category
        String query = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?"; // writing to the query

        try(Connection con = getConnection(); // connecting to the database
            PreparedStatement pS = con.prepareStatement(query)){ // preparing query to send to the database

            pS.setString(1, category.getName()); // setting parameters of sql query
            pS.setString(2, category.getDescription()); // setting parameters of sql query
            pS.setInt(3, categoryId); // setting parameters of sql query

            int rows = pS.executeUpdate(); // executes query

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } // don't need to return anything !!
    }

    @Override
    public void delete(int categoryId)
    {
        // delete category
        String query = "DELETE FROM categories WHERE category_id = ?"; //writing to the query

        try(Connection con = getConnection(); // connecting to the database
            PreparedStatement pS = con.prepareStatement(query)){ // preparing query to send to the database

            pS.setInt(1,categoryId); // setting parameter of sql query

            int rows = pS.executeUpdate(); // executes query

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    } // doesn't need to return anything!!

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
