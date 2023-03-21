<html>
<head>
<title>Peliculas</title>
</head>
<body>
<table border="1">
{
for $peliculasGen in doc("Peliculas2017.xml")//pelicula[titulo/text() = 'Blade runner']

let $titol := $peliculasGen/titulo/text()
let $any := $peliculasGen/fecha/text()
let $duracio := $peliculasGen/duracion/text()
order by $any, $titol
return
<resultat>
<tr>
<td>
<b>{$titol}</b>
<ul>
<li>Any: {$any}</li>
<li>Duracio: {$duracio}</li>
</ul>
</td>
</tr>
</resultat>
}
</table>
</body>
</html>
