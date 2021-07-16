//package com.makeevrserg.empireprojekt.events.blocks.noteblock
//
//import org.bukkit.Instrument
//import org.bukkit.block.data.type.NoteBlock
//
//
//public class NoteBlockAPI{
//    companion object{
//        public fun getInstrumentByData(data:Int): Instrument = Instrument.values()[data / 24 + 1]
//        public fun getNoteByData(data:Int): Int = data - 24 * (data / 24)
//
//        public fun getDataByNoteBlock(noteBlock: NoteBlock) = Instrument.values().indexOf(noteBlock.instrument) +  noteBlock.note.id -1
//
//    }
//}