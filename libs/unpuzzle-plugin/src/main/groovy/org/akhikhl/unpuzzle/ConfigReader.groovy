/*
 * unpuzzle
 *
 * Copyright 2014  Andrey Hihlovskiy.
 *
 * See the file "LICENSE" for copying and usage permission.
 */
package org.akhikhl.unpuzzle

/**
 *
 * @author akhikhl
 */
class ConfigReader {

  Config readFromResource(String resourceName) {
    Config config = new Config()
    Binding binding = new Binding()
    binding.unpuzzle = { Closure closure ->
      closure.resolveStrategy = Closure.DELEGATE_FIRST
      closure.delegate = config
      closure()
    }
    binding.current_os = PlatformConfig.current_os
    binding.current_arch = PlatformConfig.current_arch
    GroovyShell shell = new GroovyShell(binding)
    this.getClass().getResourceAsStream(resourceName).withReader('UTF-8') {
      shell.evaluate(it)
    }
    return config
  }
}

