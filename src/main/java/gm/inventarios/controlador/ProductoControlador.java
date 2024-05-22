package gm.inventarios.controlador;

import gm.inventarios.exception.RecursoNoEncontradoException;
import gm.inventarios.modelo.Producto;

import gm.inventarios.servicio.ProductoServicio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("inventarios-app")
@CrossOrigin(value = "http://localhost:4200")
public class ProductoControlador {


    private static final Logger logger =
            LoggerFactory.getLogger(ProductoControlador.class);

    @Autowired
    private ProductoServicio productoServicio;


    //http://localhost:8080/inventarios-app/productos
    @GetMapping("/productos")
    public List<Producto> obtenerProducto() {

        List<Producto> productos = this.productoServicio.listarProducto();
        productos.forEach((producto -> logger.info(producto.toString())));
        return productos;

    }

    @PostMapping("/productos")
    public Producto agregarProducto(@RequestBody Producto producto) {
        logger.info("Producto a a gregar");
        this.productoServicio.guardarProducto(producto);
        return producto;

    }


    /*
        ResponseEntity<Producto> tegresa un objeto de tipo producto
        dentro de la rspuesta de tipo get
     */
    @GetMapping("productos/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(
            @PathVariable int id) {

        Producto producto = this.productoServicio.buscarProductoPorId(id);

        if (producto != null)
            return ResponseEntity.ok(producto);

        else
            throw new RecursoNoEncontradoException("No se encontro el id:" + id);
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<Producto> actualizarProducto(
            //recibe el id del objeto que querems a actualizar
            @PathVariable int id,
            //recibe el objeto producto con toda la informacion del formulario
            //para actualizar el registro y guardar en la BD
            @RequestBody Producto productoRecibido) {


        //buscamos en la bd el objeto de tipo producto
        Producto producto = this.productoServicio.buscarProductoPorId(id);
        //actualizamos con la informacion que estamos recibiendo el objeto de tipop roducto de la BD

        if(producto == null)
            throw new RecursoNoEncontradoException("No se encontro el : "+ id);

        producto.setDescripcion(productoRecibido.getDescripcion());
        producto.setPrecio(productoRecibido.getPrecio());
        producto.setExistencia(productoRecibido.getExistencia());
        //guardamos la informacion actualizada
        this.productoServicio.guardarProducto(producto);
        //respondemos con ok y con la informacion del nuevo producto que se ha guardado en la bd
        return ResponseEntity.ok(producto);

    }

    /*
      Regresa un objeto de tipo ResponseEntity

     la respuesta con una cadena de eliminado
     y una respuesta de tipo booleano que indica es eliminado o no

     respondemos con dos valores con un Map
      <cadena, boleano>
     */


    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Map<String, Boolean>>
        eliminarProducto(@PathVariable int id) {

        //buscamos el producto por id
        Producto producto = productoServicio.buscarProductoPorId(id);


        //validamos para que no regresa e la respuesta error internal 500
        //cuando no encuentra el id regresa un null arroja una exception
          if(producto == null)
              throw new RecursoNoEncontradoException("No se encontro el id: " + id);


        //producto.getIdProducto()); obtenemos el objeto de tipo producto obtenemos el id
        //es el objeto que se va eliminar de la bd
        this.productoServicio.eliminarProductoPorId(producto.getIdProducto());

        //procesamos la respuesta
        //agregamos los valores con el metodo put para poner la llave
        //y es correcto se regresa un true

        Map<String, Boolean> respuesta = new HashMap<>();
        respuesta.put("eliminado", Boolean.TRUE);
        return ResponseEntity.ok(respuesta);

    }


}
