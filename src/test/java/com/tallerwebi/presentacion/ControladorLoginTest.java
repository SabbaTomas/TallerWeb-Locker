package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.usuario.DatosLogin;
import com.tallerwebi.dominio.usuario.ServicioLogin;
import com.tallerwebi.dominio.usuario.Usuario;
import com.tallerwebi.dominio.excepcion.PasswordInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.locker.ServicioGeocodificacion;
import com.tallerwebi.util.MD5Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ControladorLoginTest {

	private ControladorLogin controladorLogin;
	private Usuario usuarioMock;
	private DatosLogin datosLoginMock;
	private HttpServletRequest requestMock;
	private HttpSession sessionMock;
	private ServicioLogin servicioLoginMock;

	@BeforeEach
	public void init(){
		datosLoginMock = new DatosLogin("test@unlam.com", "Password1234");
		usuarioMock = mock(Usuario.class);
		when(usuarioMock.getEmail()).thenReturn("test@unlam.com");
		requestMock = mock(HttpServletRequest.class);
		sessionMock = mock(HttpSession.class);
		servicioLoginMock = mock(ServicioLogin.class);
		controladorLogin = new ControladorLogin(servicioLoginMock);
		ServicioGeocodificacion servicioGeocodificacion = mock(ServicioGeocodificacion.class);
	}

	@Test
	public void loginConUsuarioYPasswordInorrectosDeberiaLlevarALoginNuevamente(){
		// preparacion
		when(servicioLoginMock.consultarUsuario(anyString(), anyString())).thenReturn(null);

		// ejecucion
		ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

		// validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("login"));
		assertThat(modelAndView.getModel().get("error").toString(), equalToIgnoringCase("Usuario o clave incorrecta"));
		verify(sessionMock, times(0)).setAttribute("ROL", "ADMIN");
	}

	@Test
	public void loginConUsuarioYPasswordCorrectosDeberiaLLevarAHome(){
		// preparacion
		Usuario usuarioEncontradoMock = mock(Usuario.class);
		when(usuarioEncontradoMock.getRol()).thenReturn("ADMIN");

		when(requestMock.getSession()).thenReturn(sessionMock);
		when(servicioLoginMock.consultarUsuario(anyString(), anyString())).thenReturn(usuarioEncontradoMock);

		// ejecucion
		ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

		// validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/home"));
		verify(sessionMock, times(1)).setAttribute("ROL", usuarioEncontradoMock.getRol());
	}

	@Test
	public void registrarmeSiUsuarioNoExisteDeberiaCrearUsuarioYVolverAlLogin() throws UsuarioExistente {
		// preparacion
		Usuario nuevoUsuario = new Usuario();
		nuevoUsuario.setEmail("nuevoUsuario@unlam.com");
		nuevoUsuario.setPassword("password1234");

		when(servicioLoginMock.consultarUsuarioPorEmail("nuevoUsuario@unlam.com")).thenReturn(null);

		// ejecucion
		ModelAndView modelAndView = controladorLogin.registrarme(nuevoUsuario);

		// validacion
		assertEquals("redirect:/login", modelAndView.getViewName());
	}

	@Test
	public void registrarmeSiUsuarioExisteDeberiaVolverAFormularioYMostrarError() throws UsuarioExistente {
		// Preparación
		Usuario usuarioExistente = new Usuario();
		usuarioExistente.setEmail("usuarioExistente@unlam.com");
		usuarioExistente.setPassword("password1234");

		when(servicioLoginMock.consultarUsuarioPorEmail("usuarioExistente@unlam.com")).thenReturn(usuarioExistente);

		// Ejecución
		ModelAndView modelAndView = controladorLogin.registrarme(usuarioExistente);

		// Validación
		assertEquals("nuevo-usuario", modelAndView.getViewName());
		assertTrue(modelAndView.getModel().containsKey("error"));
		assertEquals("El usuario ya existe", modelAndView.getModel().get("error"));
	}

	@Test
	public void registrarDeberiaEncriptarLaPasswordAntesDeGuardarAlUsuario() throws UsuarioExistente {
		//preparacion
		Usuario usuarioNuevo = new Usuario();
		usuarioNuevo.setEmail("nuevo@unlam.com");
		usuarioNuevo.setPassword("password1234");

		// Ejecución
		controladorLogin.registrarme(usuarioNuevo);

		// Validación
		ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
		verify(servicioLoginMock).registrar(usuarioCaptor.capture());

		Usuario usuarioRegistrado = usuarioCaptor.getValue();
		String hashedPassword = MD5Util.hash("password1234");

		assertEquals("nuevo@unlam.com", usuarioRegistrado.getEmail());
		assertEquals(hashedPassword, usuarioRegistrado.getPassword());
	}

	@Test
	public void loginConPasswordIncorrectoNoDeberiaPermitirElAcceso() {
		//preparacion
		String passwordPlana = "password1234";
		String passwordIncorrecta = "incorrecta1234";
		String hashedPassword = MD5Util.hash(passwordIncorrecta);
		when(servicioLoginMock.consultarUsuario("test@unlam.com", hashedPassword)).thenReturn(null);

		//ejecucion
		datosLoginMock.setPassword(passwordIncorrecta);
		ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

		//validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("login"));
		assertThat(modelAndView.getModel().get("error").toString(), equalToIgnoringCase("Usuario o clave incorrecta"));
	}

	@Test
	public void queSePuedaRegistrarmeSiUsuarioNoExisteConPasswordEncriptadaDeberiaCrearUsuarioYVolverAlLogin() throws UsuarioExistente {
		//preparacion
		Usuario usuarioNuevo = new Usuario();
		usuarioNuevo.setEmail("nuevo@unlam.com");
		String passwordPlana = "password1234";
		String hashedPassword = MD5Util.hash(passwordPlana);
		usuarioNuevo.setPassword(passwordPlana);

		doAnswer(invocation -> {
			Usuario user = invocation.getArgument(0);
			user.setPassword(hashedPassword);
			return null;
		}).when(servicioLoginMock).registrar(any(Usuario.class));

		//ejecucion
		ModelAndView modelAndView = controladorLogin.registrarme(usuarioNuevo);

		//validacion
		verify(servicioLoginMock, times(1)).registrar(argThat(usuario -> hashedPassword.equals(usuario.getPassword())));
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));

	}

	@Test
	public void consultarUsuarioDeberiaDevolverUnUsuarioSiPasswordCoincide() {
		//preparacion
		String passwordPlana = "password1234";
		String hashedPassword = MD5Util.hash(passwordPlana);
		when(servicioLoginMock.consultarUsuario("test@unlam.com", hashedPassword)).thenReturn(usuarioMock);

		//ejecucion
		Usuario usuario = servicioLoginMock.consultarUsuario("test@unlam.com", hashedPassword);

		//validacion
		assertEquals(usuarioMock, usuario);
	}

	@Test
	public void queRegistrarConUnaPasswordSinEncriptarNoDeberiaPermitirElAcceso() throws UsuarioExistente {
		//preparacion
		Usuario usuarioNuevo = new Usuario();
		usuarioNuevo.setEmail("nuevo@unlam.com");
		usuarioNuevo.setPassword("password1234");

		doThrow(new UsuarioExistente()).when(servicioLoginMock).registrar(usuarioNuevo);

		//ejecucion
		ModelAndView modelAndView = controladorLogin.registrarme(usuarioNuevo);

		//validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("nuevo-usuario"));
		assertThat(modelAndView.getModel().get("error").toString(), equalToIgnoringCase("El usuario ya existe"));
	}

	@Test
	public void queSeLanzeExceptionAlRegistrarmeConPasswordInvalida() throws PasswordInvalido, UsuarioExistente {
		// preparacion
		Usuario usuarioMock = new Usuario();
		usuarioMock.setEmail("test@unlam.com");
		usuarioMock.setPassword("abc123");

		doThrow(PasswordInvalido.class).when(servicioLoginMock).registrar(usuarioMock);

		// ejecucion
		ModelAndView modelAndView = controladorLogin.registrarme(usuarioMock);

		// verificacion
		assertThrows(PasswordInvalido.class, () -> servicioLoginMock.registrar(usuarioMock));
		assertEquals("nuevo-usuario", modelAndView.getViewName());
		ModelMap modelMap = modelAndView.getModelMap();
		assertTrue(modelMap.containsAttribute("error"));
		assertEquals("La contraseña proporcionada no es válida. Debe tener al menos 8 caracteres y contener al menos un dígito.", modelMap.get("error"));
	}

}

	/*

	@Test
	public void loginConUsuarioYPasswordInorrectosDeberiaLlevarALoginNuevamente(){
		// preparacion
		when(servicioLoginMock.consultarUsuario(anyString(), anyString())).thenReturn(null);

		// ejecucion
		ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

		// validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("login"));
		assertThat(modelAndView.getModel().get("error").toString(), equalToIgnoringCase("Usuario o clave incorrecta"));
		verify(sessionMock, times(0)).setAttribute("ROL", "ADMIN");
	}
	
	@Test
	public void loginConUsuarioYPasswordCorrectosDeberiaLLevarAHome(){
		// preparacion
		Usuario usuarioEncontradoMock = mock(Usuario.class);
		when(usuarioEncontradoMock.getRol()).thenReturn("ADMIN");

		when(requestMock.getSession()).thenReturn(sessionMock);
		when(servicioLoginMock.consultarUsuario(anyString(), anyString())).thenReturn(usuarioEncontradoMock);
		
		// ejecucion
		ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);
		
		// validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/home"));
		verify(sessionMock, times(1)).setAttribute("ROL", usuarioEncontradoMock.getRol());
	}

	@Test
	public void registrameSiUsuarioNoExisteDeberiaCrearUsuarioYVolverAlLogin() throws UsuarioExistente {

		// ejecucion
		ModelAndView modelAndView = controladorLogin.registrarme(usuarioMock);

		// validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
		verify(servicioLoginMock, times(1)).registrar(usuarioMock);
	}

	@Test
	public void registrarmeSiUsuarioExisteDeberiaVolverAFormularioYMostrarError() throws UsuarioExistente {
		// preparacion
		doThrow(UsuarioExistente.class).when(servicioLoginMock).registrar(usuarioMock);

		// ejecucion
		ModelAndView modelAndView = controladorLogin.registrarme(usuarioMock);

		// validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("nuevo-usuario"));
		assertThat(modelAndView.getModel().get("error").toString(), equalToIgnoringCase("El usuario ya existe"));
	}

	@Test
	public void errorEnRegistrarmeDeberiaVolverAFormularioYMostrarError() throws UsuarioExistente {
		// preparacion
		doThrow(RuntimeException.class).when(servicioLoginMock).registrar(usuarioMock);

		// ejecucion
		ModelAndView modelAndView = controladorLogin.registrarme(usuarioMock);

		// validacion
		assertThat(modelAndView.getViewName(), equalToIgnoringCase("nuevo-usuario"));
		assertThat(modelAndView.getModel().get("error").toString(), equalToIgnoringCase("Error al registrar el nuevo usuario"));
	}*/

