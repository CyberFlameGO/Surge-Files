<?php
$zipFilePath = '/var/www/html/client/Lemonade.zip';

header('Content-Type: application/zip');
header('Content-Disposition: attachment; filename="'.basename($zipFilePath).'"');
header('Content-Length: ' . filesize($zipFilePath));

readfile($zipFilePath);