package vpcli.plugin.drivingadapter;

import dagger.assisted.AssistedFactory;

@AssistedFactory
public interface TreeCommandFactory {
    TreeCommand build(String[] args);
}
