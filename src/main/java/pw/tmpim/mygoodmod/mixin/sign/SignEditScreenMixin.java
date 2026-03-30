package pw.tmpim.mygoodmod.mixin.sign;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SignEditScreen.class)
public class SignEditScreenMixin extends Screen {

  @Inject(method = "keyPressed", at = @At("HEAD"))
  private void keyPressed(char character, int keyCode, CallbackInfo ci) {
    super.keyPressed(character, keyCode);
  }
}
