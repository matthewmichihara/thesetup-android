package com.fourpool.thesetup;

final class Modules {
  static Object[] list(TheSetupApp app) {
    return new Object[] {
        new TheSetupModule(app)
    };
  }

  private Modules() {
    // No instances.
  }
}