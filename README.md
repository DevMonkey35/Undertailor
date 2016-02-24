# Undertailor

\<insert some nice icon here>

Hey there! This is my game engine project, an engine focused on the stuffs you can do in the Undertale game. If you're up for making a fangame, then look no further! Undertailor can help!

**Undertailor is heavily based on the amazing game by Toby Fox, UNDERTALE. Go check it out [here!](http://store.steampowered.com/app/391540/)**

## What is?

If you didn't get it from the first line, this project is aimed at creating a fully-customizable engine with tools and utilities to help anyone create an Undertale fangame. Of course, you can create something else with the engine, but you'll be on your own for creating base assets from scratch (its unlikely, but possible that the stock assets I create here'll fit with your other game).

Undertailor is **mostly** singlethreaded as of now, and probably pretty inefficient in the stuff it'll try to do (but it'll definitely improve over time!); if you're planning on making something heavier than a small RPG, you might wanna check out other, more professionally-made engines.

## Status

**Almost a full alpha!**

The overworld side of this engine is almost completely finished, as well as some base API features you'll need to build and interact with objects the engine gives you. This is only the programmatical side of things; in terms of assets, you're still on your own about trying to build them through text (with of course eventual documentation on the data structure of the asset file definitions).

#### The big things that the engine can do right now...
* interpret Lua scripting and implement objects and UIs to be used in the game
* manage its own overworld and event system
* allow you to write your Undertale-styled cutscenes using provided stock libraries

## Documentation?

Eventually. It's a pretty high priority right now, too; so you won't be waiting that long.
Some documentation is already up, and can be checked at [this page](http://xemiru.github.io/Undertailor/luadocs/). Obviously, not done; not all API methods are addressed and documented. These docs specifically explain the workings of Undertailor's API functions, they do not necessarily tell you how they should be used. Actual usage documentation will come after the Lua API documentation. The content of the documentation pages can be tracked at [this repository](https://github.com/Xemiru/Undertailor-docs). The live site does not automagically build and pull any changes to the repository; the live site may not always be up to date with the current commit of the documentation repository.

## I wanna help out!

Check out this project's issue tracker! You can send me your suggestions, ideas, and pull requests to help me get more work done! Or if you're not a person who can write Java code that well, that's fine. You can still help by grabbing a current copy of the engine and messing around with it as much as you'd like, and report bugs to me as you find them. Please be sure to search if a bug has already been reported in the issue tracker first; duplicates aren't the best thing to see!

## Who are you?

Just some casual with dreams.

No contact information yet.

## Building

TODO: build instructions
TODO: build server

## Afterword

I'd like to say how proud I am of this project; this is the first time I've attempted to build something of this scale and so far, it's going pretty good! Thanks to anyone and everyone who's supported me so far, it helps a lot!
