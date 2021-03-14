package com.example.weather7.di.components;

import dagger.Subcomponent;

@Subcomponent
public interface FLocationComponent {

    @Subcomponent.Builder
    interface Builder {
        FLocationComponent build();
    }
}
