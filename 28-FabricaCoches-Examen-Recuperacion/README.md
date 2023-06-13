3. Queremos que nuestra fabrica de coches y los camiones relacionados con el transporte sean
   asíncronos y reactivos. Para ello vamos a tener que las 2 cadenas de montaje actuarán de esta
   manera produciendo un coche (con los mismos atributos que anteriormente hemos indicado) cada 1s.
   Por otro lado tenemos las dos líneas de cargas, que procesarán conjuntamente la fabricación de los
   coches, solo que una de ella comenzará 2 segundos después de la otra. La primera línea de carga
   procesara cada 1,5 segundo los coches “Super” y “Azules”, la segunda línea, solo los coches “Negros”.
   La jornada terminará cuando ambas lineas hayan procesado 10 coches cada una.