package cz.anophel.resharer.fs;

import java.time.LocalDateTime;

public interface IDescriptor {
	
	public long getUid();
	
	public String getName();
	
	public DescriptorTypes getType();
	
	public LocalDateTime getLastModif();
	
}
