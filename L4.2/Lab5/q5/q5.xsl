<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="/pets">
    <html>
      <head>
        <title>Pets Information</title>
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
        <h2>Pets Information</h2>
        <table>
          <tr>
            <th>Name</th>
            <th>Age</th>
            <th>Type</th>
            <th>Color</th>
          </tr>
          <xsl:apply-templates select="pet"/>
        </table>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="pet">
    <tr>
      <td><xsl:value-of select="name"/></td>
      <td><xsl:value-of select="age"/></td>
      <td><xsl:value-of select="type"/></td>
      <td><xsl:value-of select="color"/></td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
