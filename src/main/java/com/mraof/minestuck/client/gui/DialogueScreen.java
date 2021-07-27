package com.mraof.minestuck.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mraof.minestuck.MinestuckConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.lwjgl.glfw.GLFW;

public class DialogueScreen extends Screen
{
	public static final String TITLE = "minestuck.dialogue";
	private final DialogueBoxType dialogueBoxType;
	
	private static final int GUI_WIDTH = 256;
	private static final int GUI_HEIGHT = 180;
	private static final int PORTRAIT_SIZE = 32;
	
	private static final int RESPONSE_TEXT_OFFSET = 40;
	
	private String[] dialogueText;
	private String[] responseOptions;
	private ResourceLocation portrait;
	private String renderedText;
	
	private int currentTextIndex;
	private boolean doneWriting;
	private int frame;
	private int playerTextColor;
	
	public enum DialogueBoxType
	{
		STANDARD(new ResourceLocation("minestuck", "textures/gui/dialogue.png"), false),
		DARK(new ResourceLocation("minestuck", "textures/gui/dialogue_dark.png"), false); //same as dialogue but better fitted for light colored texts like iguanas
		
		public final ResourceLocation background;
		public final boolean animated;
		
		DialogueBoxType(ResourceLocation background, boolean animated)
		{
			this.background = background;
			this.animated = animated; //currently no plans for this, but if we wanted to make it so the box has its own animation then this could be used to check for it(feel free to remove)
		}
		
		public final ResourceLocation getTextBackgroundBox()
		{
			return background;
		}
	}
	
	public DialogueScreen(String[] dialogueText, ResourceLocation portrait, String[] responseOptions, int color, DialogueBoxType dialogueBoxType)
	{
		super(new TranslationTextComponent(TITLE));
		this.dialogueText = dialogueText;
		this.portrait = portrait;
		this.responseOptions = responseOptions;
		this.playerTextColor = color;
		this.currentTextIndex = 0;
		this.dialogueBoxType = dialogueBoxType;
		resetWriter();
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		this.renderBackground();
		
		int xOffset = (width - GUI_WIDTH) / 2;
		int yOffset = (height - GUI_HEIGHT) / 2;
		
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		this.minecraft.getTextureManager().bindTexture(dialogueBoxType.getTextBackgroundBox()); //sets the backdrop for the text, by default it will use gui/dialogue.png
		this.blit(xOffset, yOffset, 0, 0, GUI_WIDTH, GUI_HEIGHT);
		
		int leftStart = xOffset + PORTRAIT_SIZE + 16;
		int topStart = yOffset + GUI_HEIGHT + 8;
		int lineHeight = 10;
		
		font.drawSplitString(renderedText, leftStart, yOffset + 12, GUI_WIDTH - PORTRAIT_SIZE - 28, 0x000000);
		
		this.minecraft.getTextureManager().bindTexture(portrait);
		this.blit(xOffset + 8, yOffset + 44, getAnimationOffset() * PORTRAIT_SIZE, 0, PORTRAIT_SIZE, PORTRAIT_SIZE);
		
		super.render(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void init()
	{
		super.init();
		
		int xOffset = ((width - GUI_WIDTH) / 2) + PORTRAIT_SIZE + 16 - RESPONSE_TEXT_OFFSET;
		int yOffset = ((height - GUI_HEIGHT) / 2) + GUI_HEIGHT + 8;
		
		for(int i = 0; i < responseOptions.length; i++)
		{
			addButton(new DialogueButton(xOffset, yOffset + (10 * i) - 70, GUI_WIDTH, 10, responseOptions[i], btn -> test())); //slightly off in x coords from the speaker dialogue intentionally
		}
		
		this.changeFocus(true);
	}
	
	public void test()
	{
	
	}
	
	@Override
	public void tick()
	{
		if(!doneWriting)
		{
			String text = dialogueText[currentTextIndex];
			int amount = Math.min(frame * MinestuckConfig.CLIENT.dialogueSpeed.get(), text.length()); //default dialogue speed is 6
			
			if(amount == text.length())
			{
				doneWriting = true;
			}
			
			renderedText = text.substring(0, amount);
			frame++;
		}
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int i)
	{
		if(keyCode == GLFW.GLFW_KEY_SPACE && !this.doneWriting)
		{
			skip();
			return false;
		}
		
		if(keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_W)
		{
			if(!this.changeFocus(false))
			{
				this.changeFocus(false);
			}
			return true;
		}
		
		if(keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_S)
		{
			if(!this.changeFocus(true))
			{
				this.changeFocus(true);
			}
			return true;
		}
		
		return super.keyPressed(keyCode, scanCode, i);
	}
	
	private int getAnimationOffset()
	{
		if(!this.doneWriting)
			return (this.frame % 4) / 2;
		
		return 0;
	}
	
	private void resetWriter()
	{
		doneWriting = false;
		renderedText = "";
		frame = 1;
	}
	
	private void skip()
	{
		if(!doneWriting)
		{
			renderedText = dialogueText[currentTextIndex];
			doneWriting = true;
		} else if(currentTextIndex >= dialogueText.length - 1)
		{
			close();
		} else
		{
			currentTextIndex++;
			resetWriter();
		}
	}
	
	private void close()
	{
		this.minecraft.displayGuiScreen((Screen) null);
	}
	
	class DialogueButton extends ExtendedButton
	{
		
		DialogueButton(int xPos, int yPos, int width, int height, String displayString, IPressable handler)
		{
			super(xPos, yPos, width, height, displayString, handler);
		}
		
		@Override
		public void renderButton(int mouseX, int mouseY, float partial)
		{
			Minecraft mc = Minecraft.getInstance();
			String buttonText = this.getMessage();
			int color = 0xAFAFAF;
			int offset = 0;
			
			if(isHovered)
			{
				color = playerTextColor;
			}
			
			if(isFocused())
			{
				buttonText = "> " + buttonText;
				color = playerTextColor;
				offset = 0;
			}
			
			this.drawString(mc.fontRenderer, buttonText, this.x - offset, this.y, color);
		}
	}
}
