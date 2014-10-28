<?php

$link = mysql_pconnect("localhost", "keyDiscovery", "keyDiscovery") or die ("ERROR: Could not connect to database");
mysql_select_db("keyDiscovery", $link) or die("ERROR: Couldn't select RUBiS database");

$min = 1;
$max = 6;
for($i = 0; $i < 40; $i++) {
$val1 = rand($min,$max);
$val2 = rand($min,$max);
$val3 = rand($min,$max);
$val4 = rand($min,$max);
$val5 = rand($min,$max);
	mysql_query("INSERT INTO data VALUES ($val1, $val2, $val3, $val4, $val5)", $link);
}


mysql_close($link);
?>
