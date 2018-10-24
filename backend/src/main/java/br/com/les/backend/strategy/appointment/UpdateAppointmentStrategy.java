package br.com.les.backend.strategy.appointment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.les.backend.dao.GenericDAO;
import br.com.les.backend.entity.Appointment;
import br.com.les.backend.entity.AppointmentRequest;
import br.com.les.backend.entity.Parameter;
import br.com.les.backend.repository.AppointmentRepository;
import br.com.les.backend.repository.AppointmentRequestRepository;
import br.com.les.backend.repository.ParameterRepository;
import br.com.les.backend.strategy.IApplicationStrategy;
import br.com.les.backend.utils.Result;
import br.com.les.backend.utils.Util;
@Component
public class UpdateAppointmentStrategy implements IApplicationStrategy<Appointment> {

	@Autowired ParameterRepository parameterRepository;
	@Autowired AppointmentRepository appointmentRepository;
	@Autowired AppointmentRequestRepository appointmentRequestRepository;
	@Autowired GenericDAO<Appointment> appointmentDAO;
	
	
	@Override
	public Result<Appointment> execute(Appointment appointment) {

		Result<Appointment> result = new Result<>();
		
		// find the active parameter in the system, it need's to have only one in this state
		List<Parameter> parameterList = parameterRepository.findByActive();
		if(!parameterList.isEmpty()) {
		
			Parameter parameter = parameterList.get(0);
			
			// find the appointment that need to be changed the list needs to show only one
			Optional<Appointment> dbAppointment = appointmentRepository.findById(appointment.getId());
			
			// find which field need to be updated 
			Method methodToUpdate = findFieldAndValueToChange(dbAppointment.get(), appointment);
			Method methodToCompare;
			int timeDiference = 0;
			LocalTime dateToUpdate = null;
			LocalTime dateToCompare = null;
			try {
				methodToCompare = dbAppointment.get().getClass().getDeclaredMethod(methodToUpdate.getName());
				// verify if the value can be updated with the parameter object 
				dateToUpdate = (LocalTime) methodToUpdate.invoke(dbAppointment.get());
				dateToCompare = (LocalTime) methodToCompare.invoke(appointment);
				timeDiference = (int) ChronoUnit.HOURS.between(dateToUpdate, dateToCompare);
			} catch (Exception e) {
				e.printStackTrace();
				result.setError("Erro durante o processamento!");
				return result;
			}
			
			// validate with the list of requests if it's a change to this column was already requested
			List<AppointmentRequest> appointmentRequestList = appointmentRequestRepository.findByIdAppointment(appointment.getId());
			for (AppointmentRequest appointmentRequest : appointmentRequestList) {
				if(!appointmentRequest.getIsAproved() && appointmentRequest.getFieldToChange().equals(methodToUpdate.getName())) {
					result.setError("Já existe uma solicitação em andamento para o apontamento informado!");
					return result;
				}
			}
			
			if(timeDiference > parameter.getRetroactiveAppointmentLimitTime()
					|| timeDiference < parameter.getRetroactiveAppointmentLimitTime() * - 1) {
				//save a new AppointmentRequest and send a message back to the user
				if(result.isSuccess()) {
					AppointmentRequest appointmentRequest = new AppointmentRequest();
					appointmentRequest.setAppointment(appointment);
					appointmentRequest.setFieldToChange(methodToUpdate.getName());
					appointmentRequest.setReplacement(dateToUpdate);
					appointmentRequest.setIsAproved(false);
					appointmentRequestRepository.save(appointmentRequest);
					result.setError(Util.UPDATE_APPOINTMENT_REQUESTED);
				}
			} else {
				result.setSuccess("Apontamento atualizado com sucesso!");
			}
			// else update the requested appointment
		} else {
			result.setError(Util.ERROR_NOT_VALID_PARAMETER);
		}
		return result;
	}

	private Method findFieldAndValueToChange(Appointment dbAppointment, Appointment appointment) {
		Method[] dbMethods = dbAppointment.getClass().getDeclaredMethods();
		Method[] methods = appointment.getClass().getDeclaredMethods();
		Method valueToChange = null;
		for (Method method : methods) {
			for (Method dbMethod : dbMethods) {
				try {
					if(method.getReturnType() == LocalTime.class && dbMethod.getReturnType() == LocalTime.class 
							&& dbMethod.getName().equals(method.getName()) && method.invoke(appointment) != null
							&& !dbMethod.invoke(dbAppointment).equals(method.invoke(appointment))) {
							valueToChange = method;
							break;
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) { e.printStackTrace(); }
			}
			if(valueToChange != null) 
				break;
		}
		return valueToChange;
	}
}
