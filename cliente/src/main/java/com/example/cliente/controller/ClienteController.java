package com.example.cliente.controller;

import com.example.cliente.model.Cliente;
import com.example.cliente.model.ClienteDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.example.cliente.repository.ClienteRepository;

import java.util.Date;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

	@Autowired
	private ClienteRepository repo;

	@GetMapping({"","/"})
	public String getClientes(Model model) {
		var clientes = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));

		model.addAttribute("clientes", clientes);

		return "clientes/index";
	}

	@GetMapping("/criar")
	public String criarCliente(Model model) {
		ClienteDTO dto = new ClienteDTO();
		model.addAttribute("dto", dto);
		return "clientes/criar";
	}

	@PostMapping("/criar")
	public String criarCliente(@Valid @ModelAttribute("dto") ClienteDTO dto,
							   BindingResult result) {
		// E-mail já cadastrado
		if(repo.findByEmail(dto.getEmail()) != null) {
			result.addError(new FieldError("dto", "email", dto.getEmail(),
			false, null, null, "E-mail já cadastrado"));

			return"clientes/criar";
		}
		// Campos nulos
		if(result.hasErrors()) {
			return "clientes/criar";
		}

		Cliente cliente = new Cliente();
		cliente.setNome(dto.getNome());
		cliente.setSobrenome(dto.getSobrenome());
		cliente.setEmail(dto.getEmail());
		cliente.setCriado(new Date());

		repo.save(cliente);

		return "redirect:/clientes";
	}

	@GetMapping("/editar")
	public String editarCliente(@RequestParam int id, Model model){

		Cliente cliente = repo.findById(id).orElse(null);

		if(cliente == null) {
			return "redirect:/clientes";
		}

		ClienteDTO dto = new ClienteDTO();
		dto.setNome(cliente.getNome());
		dto.setSobrenome(cliente.getSobrenome());
		dto.setEmail(cliente.getEmail());
		model.addAttribute("cliente", cliente);
		model.addAttribute("dto", dto);

		return "clientes/editar";
	}

	@PostMapping("/editar")
	public String editarCliente(
			@RequestParam int id, @Valid @ModelAttribute("dto") ClienteDTO dto,
			BindingResult result, Model model) {

		Cliente cliente = repo.findById(id).orElse(null);

		model.addAttribute("cliente", cliente);

		if(cliente == null) return "redirect:/clientes";

		if(result.hasErrors()) return "clientes/editar";

		cliente.setNome(dto.getNome());
		cliente.setSobrenome(dto.getSobrenome());
		cliente.setEmail(dto.getEmail());
		try {
			repo.save(cliente);
		} catch (Exception e) {
			result.addError(
					new FieldError("dto","email",dto.getEmail(), false, null,
					null, "E-mail já cadastrado"));
			return "clientes/editar";
		}

		return "redirect:/clientes";
	}
	
	@GetMapping("/apagar")
	public String apagarCliente(@RequestParam int id) {

		Cliente cliente = repo.findById(id).orElse(null);

		if(cliente != null) {
			repo.delete(cliente);
		}

		return "redirect:/clientes";
	}
	
	
}
