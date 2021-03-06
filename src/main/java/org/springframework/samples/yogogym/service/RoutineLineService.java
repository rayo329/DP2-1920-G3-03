/*
	 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.yogogym.service;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.yogogym.model.RoutineLine;
import org.springframework.samples.yogogym.model.Training;
import org.springframework.samples.yogogym.repository.RoutineLineRepository;
import org.springframework.samples.yogogym.service.exceptions.TrainingFinished;
import org.springframework.samples.yogogym.service.exceptions.TrainingRepAndTimeSetted;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Mostly used as a facade for all Petclinic controllers Also a placeholder
 * for @Transactional and @Cacheable annotations
 *
 * @author Michael Isvy
 */
@Service
public class RoutineLineService {

	private RoutineLineRepository routineLineRepository;
	private TrainingService trainingService;

	@Autowired
	public RoutineLineService(RoutineLineRepository routineLineRepository, TrainingService trainingService) {
		this.routineLineRepository = routineLineRepository;
		this.trainingService = trainingService;
	}
	
	@Transactional
	public RoutineLine findRoutineLineById(int routineLineId) throws DataAccessException {
		return this.routineLineRepository.findRoutineLineById(routineLineId);
	}
	
	public Collection<RoutineLine> findAllRoutinesLines() throws DataAccessException
	{
		return this.routineLineRepository.findAllRoutines();
	}
	
	@Transactional(rollbackFor= {TrainingFinished.class})
	public void deleteRoutineLine(RoutineLine routineLine,int trainingId) throws DataAccessException, TrainingFinished {
		
		Training training = this.trainingService.findTrainingById(trainingId);
		
		Calendar cal = Calendar.getInstance();
		Date actualDate = cal.getTime();
		
		if(training.getEndDate().before(actualDate))
			throw new TrainingFinished();
		else
			this.routineLineRepository.delete(routineLine);
	}
	
	@Transactional(rollbackFor= {TrainingFinished.class, TrainingRepAndTimeSetted.class})
	public void saveRoutineLine(RoutineLine routineLine, int trainingId) throws DataAccessException,TrainingFinished, TrainingRepAndTimeSetted {
		
		Training training = this.trainingService.findTrainingById(trainingId);
		
		Calendar cal = Calendar.getInstance();
		Date actualDate = cal.getTime();
		
		if(training.getEndDate().before(actualDate))
			throw new TrainingFinished();
		else if(routineLine.getTime() != null && routineLine.getReps() != null)
			throw new TrainingRepAndTimeSetted();
		else
			this.routineLineRepository.save(routineLine);
	}
}
