package vpcli.plugin.drivingadapter;

import dagger.assisted.AssistedFactory;

@AssistedFactory
public interface ExportCommandFactory {
    ExportCommand build(String[] args);
}
