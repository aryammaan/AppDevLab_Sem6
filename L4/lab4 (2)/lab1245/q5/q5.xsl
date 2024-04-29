<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="/">
    <html>
      <head>
        <title>Students Information</title>
        <style>
          table {
            width: 100%;
            border-collapse: collapse;
          }
          th, td {
            border: 1px solid black;
            padding: 8px;
            text-align: left;
          }
          th {
            background-color: #f2f2f2;
          }
        </style>
      </head>
      <body>
        <h1>Students Information</h1>
        <table>
          <tr>
            <th>Isbn</th>
            <th>Title</th>
            <th>Author</th>
          </tr>
      <xsl:for-each select="Books/Book">
        <tr>
          <td><xsl:value-of select="Isbn"/></td>
          <td><xsl:value-of select="Title"/></td>
          <td><xsl:value-of select="Author"/></td>
        </tr>
      </xsl:for-each>
        </table>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>