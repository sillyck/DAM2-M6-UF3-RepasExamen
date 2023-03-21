for $peli in doc("Peliculas2017.xml")//pelicula/titulo[matches(text(),'La', 'i')]/text()
order by $peli
return $peli
