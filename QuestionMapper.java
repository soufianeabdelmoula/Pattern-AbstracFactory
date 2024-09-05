package fr.vdm.referentiel.refadmin.mapper;

import fr.vdm.referentiel.refadmin.dto.OffreRequestDto;
import fr.vdm.referentiel.refadmin.dto.QuestionDto;
import fr.vdm.referentiel.refadmin.model.Offre;
import fr.vdm.referentiel.refadmin.model.Question;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper( uses = {}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface QuestionMapper {

    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    @Mappings({})
    QuestionDto questionToQuestionDto(Question question);

    @Mappings({})
    List<QuestionDto> questionsToQuestionsDtos(List<Question> questions);

    List<Question> questionsDtosToQuestions(List<QuestionDto> questionDtoList);

    void updateQuestionFromDto(@MappingTarget Question existingQuestion, QuestionDto questionDto);

}