package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.usuario.DatosLogin;
import com.tallerwebi.dominio.usuario.ServicioLogin;
import com.tallerwebi.dominio.usuario.Usuario;
import com.tallerwebi.dominio.usuario.excepciones.UsuarioExistente;
import com.tallerwebi.util.MD5Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ControladorLoginTest {

	private ControladorLogin controladorLogin;
	private Usuario usuarioMock;
	private DatosLogin datosLoginMock;
	private HttpServletRequest requestMock;
	private HttpSession sessionMock;
	private ServicioLogin servicioLoginMock;

	@BeforeEach
	public void init() {
		datosLoginMock = new DatosLogin("test@unlam.com", "Password1234");
		usuarioMock = mock(Usuario.class);
		requestMock = mock(HttpServletRequest.class);
		sessionMock = mock(HttpSession.class);
		servicioLoginMock = mock(ServicioLogin.class);
		controladorLogin = new ControladorLogin(servicioLoginMock);
	}

	@Test
	public void dadoQueUsuarioYPasswordIncorrectosAlValidarLoginDeberiaLlevarALogin() {
		when(servicioLoginMock.consultarUsuario(anyString(), anyString())).thenReturn(null);

		ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

		assertThat(modelAndView.getViewName(), equalToIgnoringCase("login"));
		assertThat(modelAndView.getModel().get("error").toString(), equalToIgnoringCase("Usuario o clave incorrecta"));
		verify(sessionMock, times(0)).setAttribute("ROL", "ADMIN");
	}

	@Test
	public void dadoQueUsuarioYPasswordCorrectosAlValidarLoginDeberiaLlevarAHome() {
		Usuario usuarioEncontradoMock = mock(Usuario.class);
		when(usuarioEncontradoMock.getRol()).thenReturn("ADMIN");
		when(requestMock.getSession()).thenReturn(sessionMock);
		when(servicioLoginMock.consultarUsuario(anyString(), anyString())).thenReturn(usuarioEncontradoMock);

		ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

		assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/home"));
		verify(sessionMock, times(1)).setAttribute("ROL", usuarioEncontradoMock.getRol());
	}

	@Test
	public void dadoQueUsuarioNoExisteAlRegistrarmeDeberiaCrearUsuarioYVolverAlLogin() throws UsuarioExistente {
		Usuario nuevoUsuario = new Usuario();
		nuevoUsuario.setEmail("nuevoUsuario@unlam.com");
		nuevoUsuario.setPassword("password1234");

		when(servicioLoginMock.consultarUsuarioPorEmail("nuevoUsuario@unlam.com")).thenReturn(null);

		ModelAndView modelAndView = controladorLogin.registrarme(nuevoUsuario);

		assertEquals("redirect:/login", modelAndView.getViewName());
	}

	@Test
	public void dadoQueUsuarioExisteAlRegistrarmeDeberiaVolverAFormularioYMostrarError() throws UsuarioExistente {
		Usuario usuarioExistente = new Usuario();
		usuarioExistente.setEmail("usuarioExistente@unlam.com");
		usuarioExistente.setPassword("password1234");

		when(servicioLoginMock.consultarUsuarioPorEmail("usuarioExistente@unlam.com")).thenReturn(usuarioExistente);

		ModelAndView modelAndView = controladorLogin.registrarme(usuarioExistente);

		assertEquals("nuevo-usuario", modelAndView.getViewName());
		assertTrue(modelAndView.getModel().containsKey("error"));
		assertEquals("El usuario ya existe", modelAndView.getModel().get("error"));
	}

	@Test
	public void dadoQuePasswordNoEncriptadaAlRegistrarDeberiaEncriptarPasswordAntesDeGuardarUsuario() throws UsuarioExistente {
		Usuario usuarioNuevo = new Usuario();
		usuarioNuevo.setEmail("nuevo@unlam.com");
		usuarioNuevo.setPassword("password1234");

		controladorLogin.registrarme(usuarioNuevo);

		ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
		verify(servicioLoginMock).registrar(usuarioCaptor.capture());

		Usuario usuarioRegistrado = usuarioCaptor.getValue();
		String hashedPassword = MD5Util.hash("password1234");

		assertEquals("nuevo@unlam.com", usuarioRegistrado.getEmail());
		assertEquals(hashedPassword, usuarioRegistrado.getPassword());
	}

	@Test
	public void dadoQuePasswordIncorrectaAlValidarLoginDeberiaMostrarError() {
		String passwordIncorrecta = "pass";
		Usuario usuario = new Usuario();
		usuario.setEmail("test@unlam.com");
		usuario.setPassword(passwordIncorrecta);

		ModelAndView modelAndView = controladorLogin.registrarme(usuario);

		assertEquals("nuevo-usuario", modelAndView.getViewName());
		assertTrue(modelAndView.getModel().containsKey("error"));
		assertEquals("La contraseña proporcionada no es válida. Debe tener al menos 8 caracteres y contener al menos un dígito.", modelAndView.getModel().get("error"));
	}

	@Test
	public void dadoQueSesionNoIniciadaAlIrAHomeDeberiaRedirigirALogin() {
		when(requestMock.getSession()).thenReturn(sessionMock);
		when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(null);

		ModelAndView modelAndView = controladorLogin.irAHome(requestMock);

		assertEquals("redirect:/login", modelAndView.getViewName());
	}

	@Test
	public void dadoQueSesionIniciadaAlIrAHomeDeberiaLlevarAHome() {
		when(requestMock.getSession()).thenReturn(sessionMock);
		when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(1L);

		ModelAndView modelAndView = controladorLogin.irAHome(requestMock);

		assertEquals("home", modelAndView.getViewName());
	}

	@Test
	public void dadoQueSeLlamaInicioDeberiaRedirigirALogin() {
		ModelAndView modelAndView = controladorLogin.inicio();
		assertEquals("redirect:/login", modelAndView.getViewName());
	}

	@Test
	public void dadoQueSeAccedeANuevoUsuarioDeberiaDevolverVistaNuevoUsuario() {
		ModelAndView modelAndView = controladorLogin.nuevoUsuario();
		assertEquals("nuevo-usuario", modelAndView.getViewName());
	}

	@Test
	public void dadoQueSeAccedeALoginDeberiaDevolverVistaLogin() {
		ModelAndView modelAndView = controladorLogin.irALogin();
		assertEquals("login", modelAndView.getViewName());
	}
}
