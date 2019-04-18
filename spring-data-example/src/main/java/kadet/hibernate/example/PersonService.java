package kadet.hibernate.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public List<PersonResponse> findAll() {
        return personRepository
                .findAll()
                .stream()
                .map(person -> new PersonResponse(person.getId()))
                .collect(toList());
    }
}
