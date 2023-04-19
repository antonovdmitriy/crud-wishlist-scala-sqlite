package com.example.wishlist.service

import com.example.wishlist.model.{WishlistItem, WishlistItemInput}

import java.sql._
import javax.sql.DataSource
import scala.math.BigDecimal.double2bigDecimal

class WishlistServiceJdbcImpl(dataSource: DataSource) extends WishlistService {

  override def getAllItems: Seq[WishlistItem] = {
    var connection: Connection       = null
    var statement: PreparedStatement = null
    var resultSet: ResultSet         = null
    try {
      connection = dataSource.getConnection()
      statement = connection.prepareStatement("SELECT id, name, description, price, link_to_order FROM wishlist")
      resultSet = statement.executeQuery()
      resultSetToList(resultSet)
    } finally {
      closeResources(connection, statement, resultSet)
    }
  }

  override def getItem(id: Int): Option[WishlistItem] = {
    var connection: Connection       = null
    var statement: PreparedStatement = null
    var resultSet: ResultSet         = null
    try {
      connection = dataSource.getConnection()
      statement = connection.prepareStatement("SELECT id, name, description, price, link_to_order FROM wishlist WHERE id = ?")
      statement.setInt(1, id)
      resultSet = statement.executeQuery()
      resultSetToList(resultSet).headOption
    } finally {
      closeResources(connection, statement, resultSet)
    }
  }

  def addItem(itemInput: WishlistItemInput): WishlistItem = {
    val connection = dataSource.getConnection()
    val preparedStatement = connection.prepareStatement(
      """
        |INSERT INTO wishlist (name, description, price, link_to_order)
        |VALUES (?, ?, ?, ?)
    """.stripMargin,
      Statement.RETURN_GENERATED_KEYS // Specify that you want to retrieve the generated keys
    )

    try {
      preparedStatement.setString(1, itemInput.name)
      preparedStatement.setString(2, itemInput.description.orNull)
      preparedStatement.setBigDecimal(3, itemInput.price.map(_.bigDecimal).orNull)
      preparedStatement.setString(4, itemInput.linkToOrder)

      preparedStatement.executeUpdate()

      val generatedKeys = preparedStatement.getGeneratedKeys
      if (generatedKeys.next()) {
        val id = generatedKeys.getInt(1)
        WishlistItem(
          id,
          itemInput.name,
          itemInput.description,
          itemInput.price,
          itemInput.linkToOrder
        )
      } else {
        throw new SQLException("Failed to retrieve generated keys")
      }
    } finally {
      closeResources(connection, preparedStatement, null)
    }
  }

  override def updateItem(
      id: Int,
      itemInput: WishlistItemInput
  ): Option[WishlistItem] = {
    var connection: Connection       = null
    var statement: PreparedStatement = null
    try {
      connection = dataSource.getConnection()
      statement =
        connection.prepareStatement("UPDATE wishlist SET name = ?, price = ? WHERE id = ?")
      statement.setString(1, itemInput.name)
      statement.setBigDecimal(2, itemInput.price.map(_.bigDecimal).orNull)
      statement.setInt(3, id)
      val updatedRows = statement.executeUpdate()
      if (updatedRows > 0) {
        Some(
          WishlistItem(
            id,
            itemInput.name,
            itemInput.description,
            itemInput.price,
            itemInput.linkToOrder
          )
        )
      } else {
        None
      }
    } finally {
      closeResources(connection, statement, null)
    }
  }

  override def deleteItem(id: Int): Int = {
    var connection: Connection       = null
    var statement: PreparedStatement = null
    try {
      connection = dataSource.getConnection()
      statement = connection.prepareStatement("DELETE FROM wishlist WHERE id = ?")
      statement.setInt(1, id)
      statement.executeUpdate()
    } finally {
      closeResources(connection, statement, null)
    }
  }

  private def resultSetToList(resultSet: ResultSet): Seq[WishlistItem] = {
    var items: Seq[WishlistItem] = Seq.empty
    while (resultSet.next()) {
      val id              = resultSet.getInt("id")
      val name            = resultSet.getString("name")
      val description     = Option(resultSet.getString("description"))
      val priceBigDecimal = resultSet.getBigDecimal("price")
      val price =
        if (priceBigDecimal != null) Some(priceBigDecimal.doubleValue())
        else None
      val linkToOrder = resultSet.getString("link_to_order")
      items :+= WishlistItem(id, name, description, price, linkToOrder)
    }
    items
  }

  private def closeResources(
      connection: Connection,
      statement: PreparedStatement,
      resultSet: ResultSet
  ): Unit = {
    if (resultSet != null) {
      resultSet.close()
    }
    if (statement != null) {
      statement.close()
    }
    if (connection != null) {
      connection.close()
    }
  }

}
