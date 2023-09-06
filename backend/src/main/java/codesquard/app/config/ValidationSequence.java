package codesquard.app.config;

import javax.validation.GroupSequence;

@GroupSequence({
	ValidationGroups.NotNullGroup.class,
	ValidationGroups.NotBlankGroup.class,
	ValidationGroups.PatternGroup.class,
	ValidationGroups.SizeGroup.class})
public interface ValidationSequence {
}
