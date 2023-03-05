for $genero in distinct-values(doc("Peliculas2017.xml")//genero/text())
order by $genero
return $genero