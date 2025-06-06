package com.pouyoute.qols;

import net.fabricmc.api.ModInitializer;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.*;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pouyouteqols implements ModInitializer {
	public static final String MOD_ID = "pouyouteqols";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Identifier UPDATE_ENCHANTMENTS_PACKET_ID = new Identifier("pouyouteqols", "update_enchantments");



	@Override
	public void onInitialize() {

		LOGGER.info("Hello Fabric world!");



	}
}